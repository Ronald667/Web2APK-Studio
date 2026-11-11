package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorScreen(
    initialUrl: String? = null,
    initialName: String? = null,
    onSaveApp: (appName: String, webUrl: String, appDescription: String, js: Boolean, zoom: Boolean, refresh: Boolean, desktop: Boolean, iconUri: String?, onDone: (Long) -> Unit) -> Unit,
    onTestPreview: (url: String, title: String) -> Unit,
    onBack: () -> Unit
) {
    var appName by remember { mutableStateOf(initialName ?: "") }
    var webUrl by remember { mutableStateOf(initialUrl ?: "https://") }
    var appDescription by remember { mutableStateOf("") }
    var enableJavaScript by remember { mutableStateOf(true) }
    var enableZoom by remember { mutableStateOf(true) }
    var pullToRefresh by remember { mutableStateOf(true) }
    var desktopMode by remember { mutableStateOf(false) }
    var selectedIconUri by remember { mutableStateOf<Uri?>(null) }
    var savedSuccess by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedIconUri = uri
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Builder & Wrapper", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Configure Your Native Android App",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter your website details and description below to build a customized, high-performance WebView wrapper application.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("App Name (e.g. My Portfolio)") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_name_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = appDescription,
                onValueChange = { appDescription = it },
                label = { Text("App Description (Short summary)") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_description_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = webUrl,
                onValueChange = { webUrl = it },
                label = { Text("Website URL (https://...)") },
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("web_url_input"),
                singleLine = true
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedIconUri != null) {
                            AsyncImage(
                                model = selectedIconUri,
                                contentDescription = "App Icon Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("App Launcher Icon", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (selectedIconUri != null) "Custom icon selected" else "Upload a PNG image for your app icon",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.testTag("upload_icon_button")
                        ) {
                            Text(if (selectedIconUri != null) "Change Icon" else "Upload PNG Icon")
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = "Advanced WebView Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SettingSwitchRow(
                title = "Enable JavaScript",
                subtitle = "Allow website scripts to execute normally",
                checked = enableJavaScript,
                onCheckedChange = { enableJavaScript = it }
            )

            SettingSwitchRow(
                title = "Enable Pinch-to-Zoom",
                subtitle = "Allow users to zoom in and out of pages",
                checked = enableZoom,
                onCheckedChange = { enableZoom = it }
            )

            SettingSwitchRow(
                title = "Pull-to-Refresh",
                subtitle = "Swipe down to reload the web page",
                checked = pullToRefresh,
                onCheckedChange = { pullToRefresh = it }
            )

            SettingSwitchRow(
                title = "Desktop User-Agent",
                subtitle = "Request desktop version of websites",
                checked = desktopMode,
                onCheckedChange = { desktopMode = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (webUrl.isNotBlank() && webUrl != "https://") {
                            onTestPreview(webUrl, appName.ifBlank { "WebView Preview" })
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("test_preview_button")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Preview")
                }

                Button(
                    onClick = {
                        if (appName.isNotBlank() && webUrl.isNotBlank() && webUrl != "https://") {
                            onSaveApp(
                                appName,
                                webUrl,
                                appDescription,
                                enableJavaScript,
                                enableZoom,
                                pullToRefresh,
                                desktopMode,
                                selectedIconUri?.toString()
                            ) {
                                savedSuccess = true
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_app_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Project")
                }
            }

            if (savedSuccess) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Column {
                            Text("App Saved Successfully!", fontWeight = FontWeight.Bold)
                            Text("You can launch it anytime from the dashboard.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingSwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
