const express = require('express');
const fs = require('fs-extra');
const path = require('path');
const { exec } = require('child_process');
const { v4: uuidv4 } = require('uuid');
const app = express();

app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));
app.use('/download', express.static(path.join(__dirname, 'builds')));

app.post('/api/build-apk', async (req, res) => {
    const { appName, webUrl } = req.body;
    if (!appName || !webUrl) {
        return res.status(400).json({ error: 'App name and URL are required.' });
    }
    const buildId = uuidv4();
    const tempDir = path.join(__dirname, 'tmp', buildId);
    const buildsDir = path.join(__dirname, 'builds');

    try {
        // 1. Copy project root to isolated build directory
        await fs.copy(__dirname, tempDir, {
            filter: (src) => {
                const basename = path.basename(src);
                return !['node_modules', '.git', 'builds', 'tmp', '.idea', '.gradle'].includes(basename);
            }
        });

        // 2. Inject user parameters into strings.xml
        const stringsPath = path.join(tempDir, 'app/src/main/res/values/strings.xml');
        if (await fs.pathExists(stringsPath)) {
            let stringsContent = await fs.readFile(stringsPath, 'utf8');
            stringsContent = stringsContent.replace(/<string name="app_name">.*?<\/string>/, `<string name="app_name">${appName}</string>`);
            await fs.writeFile(stringsPath, stringsContent);
        }

        // 3. Update target URL in MainActivity
        const mainActivityPath = path.join(tempDir, 'app/src/main/java/com/example/MainActivity.kt');
        if (await fs.pathExists(mainActivityPath)) {
            let activityContent = await fs.readFile(mainActivityPath, 'utf8');
            activityContent = activityContent.replace(/"https:\/\/[^"]+"/, `"${webUrl}"`);
            await fs.writeFile(mainActivityPath, activityContent);
        }

        // 4. Execute Gradle build command to generate real APK
        const gradlewCmd = process.platform === 'win32' ? 'gradlew.bat' : 'gradle';

        exec(`${gradlewCmd} :app:assembleDebug`, { cwd: tempDir }, async (error, stdout, stderr) => {
            if (error) {
                console.error('Build Error:', stderr || stdout);
                await fs.remove(tempDir).catch(() => {});
                return res.status(500).json({ error: 'Failed to compile APK.' });
            }

            const generatedApkPath = path.join(tempDir, 'app/build/outputs/apk/debug/app-debug.apk');
            if (await fs.pathExists(generatedApkPath)) {
                const outputApkName = `${buildId}.apk`;
                const finalApkPath = path.join(buildsDir, outputApkName);
                await fs.ensureDir(buildsDir);
                await fs.move(generatedApkPath, finalApkPath);
                await fs.remove(tempDir).catch(() => {});
                return res.json({
                    success: true,
                    downloadUrl: `/download/${outputApkName}`
                });
            } else {
                await fs.remove(tempDir).catch(() => {});
                return res.status(500).json({ error: 'APK output not found after build.' });
            }
        });
    } catch (err) {
        console.error(err);
        if (await fs.pathExists(tempDir)) await fs.remove(tempDir).catch(() => {});
        res.status(500).json({ error: 'Internal server error during build.' });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
