package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WebAppsViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object SavedApps : Screen("saved_apps", "Saved Apps", Icons.Default.Bookmarks)
    object Tools : Screen("tools", "Tools", Icons.Default.Build)
    object Creator : Screen("creator", "Creator", Icons.Default.Add)
    object Preview : Screen("preview", "Preview", Icons.Default.Visibility)
}

@Composable
fun MainApp(viewModel: WebAppsViewModel = viewModel()) {
    val savedApps by viewModel.savedApps.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    var creatorInitialUrl by remember { mutableStateOf<String?>(null) }
    var creatorInitialName by remember { mutableStateOf<String?>(null) }
    var previewUrl by remember { mutableStateOf("https://www.google.com") }
    var previewTitle by remember { mutableStateOf("WebView Preview") }

    Scaffold(
        bottomBar = {
            if (currentScreen == Screen.Dashboard || currentScreen == Screen.SavedApps || currentScreen == Screen.Tools) {
                NavigationBar {
                    val items = listOf(Screen.Dashboard, Screen.SavedApps, Screen.Tools)
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(
                    savedApps = savedApps,
                    onNavigateToCreator = { url, name ->
                        creatorInitialUrl = url
                        creatorInitialName = name
                        currentScreen = Screen.Creator
                    },
                    onLaunchPreview = { url, title ->
                        previewUrl = url
                        previewTitle = title
                        currentScreen = Screen.Preview
                    },
                    onDeleteApp = { app -> viewModel.deleteApp(app) }
                )
                Screen.SavedApps -> SavedAppsScreen(
                    savedApps = savedApps,
                    onLaunchPreview = { url, title ->
                        previewUrl = url
                        previewTitle = title
                        currentScreen = Screen.Preview
                    },
                    onDeleteApp = { app -> viewModel.deleteApp(app) }
                )
                Screen.Tools -> ToolsScreen()
                Screen.Creator -> CreatorScreen(
                    initialUrl = creatorInitialUrl,
                    initialName = creatorInitialName,
                    onSaveApp = { name, url, description, js, zoom, refresh, desktop, iconUri, onDone ->
                        viewModel.saveApp(name, url, description, "#6750A4", js, zoom, refresh, desktop, iconUri) { id ->
                            onDone(id)
                        }
                    },
                    onTestPreview = { url, title ->
                        previewUrl = url
                        previewTitle = title
                        currentScreen = Screen.Preview
                    },
                    onBack = { currentScreen = Screen.Dashboard }
                )
                Screen.Preview -> WebViewPreviewScreen(
                    url = previewUrl,
                    appTitle = previewTitle,
                    onBack = { currentScreen = Screen.Dashboard }
                )
            }
        }
    }
}
