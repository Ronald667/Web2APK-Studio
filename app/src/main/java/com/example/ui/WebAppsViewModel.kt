package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SavedAppEntity
import com.example.data.SavedAppRepository
import com.example.data.Web2ApkDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WebAppsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SavedAppRepository

    val savedApps: StateFlow<List<SavedAppEntity>>

    init {
        val dao = Web2ApkDatabase.getDatabase(application).savedAppDao()
        repository = SavedAppRepository(dao)
        savedApps = repository.allApps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun saveApp(
        appName: String,
        webUrl: String,
        appDescription: String = "",
        primaryColorHex: String = "#6750A4",
        enableJavaScript: Boolean = true,
        enableZoom: Boolean = true,
        pullToRefresh: Boolean = true,
        desktopMode: Boolean = false,
        iconUri: String? = null,
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val entity = SavedAppEntity(
                appName = appName,
                webUrl = webUrl,
                appDescription = appDescription,
                primaryColorHex = primaryColorHex,
                enableJavaScript = enableJavaScript,
                enableZoom = enableZoom,
                pullToRefresh = pullToRefresh,
                desktopMode = desktopMode,
                iconUri = iconUri
            )
            val id = repository.insert(entity)
            onComplete(id)
        }
    }

    fun deleteApp(app: SavedAppEntity) {
        viewModelScope.launch {
            repository.delete(app)
        }
    }
}
