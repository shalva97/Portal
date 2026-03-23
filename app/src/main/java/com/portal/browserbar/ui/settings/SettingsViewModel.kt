package com.portal.browserbar.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portal.browserbar.data.repository.AppRepository
import com.portal.browserbar.domain.model.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val allApps: List<AppModel> = emptyList(),
    val recentlyInstalledApps: List<AppModel> = emptyList()
)

class SettingsViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        repository.getAllApps()
            .onEach { apps ->
                _uiState.update { it.copy(allApps = apps) }
            }.launchIn(viewModelScope)

        // Apps installed in the last hour
        val oneHourAgo = System.currentTimeMillis() - 3600_000
        repository.getRecentlyInstalledApps(oneHourAgo)
            .onEach { apps ->
                _uiState.update { it.copy(recentlyInstalledApps = apps) }
            }.launchIn(viewModelScope)
    }

    fun toggleAppVisibility(packageName: String, isHidden: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setHidden(packageName, isHidden)
        }
    }
}
