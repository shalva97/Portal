package com.portal.browserbar.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portal.browserbar.data.repository.AppRepository
import com.portal.browserbar.domain.model.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: AppRepository
) : ViewModel() {

    val uiState = MutableStateFlow(SearchUiState())
    val onUIFullyLoaded = MutableStateFlow(false)

    private val _allApps = repository.getVisibleApps()

    init {
        repository.getRecentlyUsedApps()
            .onEach { apps ->
                uiState.update { it.copy(recentlyUsedApps = apps) }
                onUIFullyLoaded.value = true
            }.launchIn(viewModelScope)

        uiState
            .map { it.query }
            .debounce(100)
            .combine(_allApps) { query, apps ->
                if (query.isBlank()) {
                    emptyList()
                } else {
                    searchApps(query, apps)
                }
            }
            .onEach { results ->
                uiState.update { it.copy(searchResults = results, isSearching = uiState.value.query.isNotBlank()) }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(newQuery: String) {
        uiState.update { it.copy(query = newQuery) }
    }

    private fun searchApps(query: String, apps: List<AppModel>): List<AppModel> {
        val lowercaseQuery = query.lowercase()
        return apps.map { app ->
            val score = calculateScore(lowercaseQuery, app.label.lowercase())
            app to (score + (app.usageCount * 0.1f))
        }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun calculateScore(query: String, label: String): Float {
        if (label.startsWith(query)) return 100f
        if (label.contains(query)) return 50f
        
        // Simple fuzzy matching (nfx -> netflix)
        var score = 0f
        var queryIdx = 0
        var labelIdx = 0
        var matches = 0
        
        while (queryIdx < query.length && labelIdx < label.length) {
            if (query[queryIdx] == label[labelIdx]) {
                matches++
                queryIdx++
            }
            labelIdx++
        }
        
        if (matches == query.length) {
            score = 25f + (matches.toFloat() / label.length.toFloat() * 10f)
        }
        
        return score
    }

    fun launchApp(app: AppModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.incrementUsage(app.packageName)
            repository.launchApp(app.packageName)
            uiState.update { it.copy(query = "") } // Reset search after launch
        }
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setHidden(packageName, true)
        }
    }

    fun uninstallApp(packageName: String) {
        repository.uninstallApp(packageName)
    }

    fun openInPlayStore(packageName: String) {
        repository.openInPlayStore(packageName)
    }

    fun openAppInfo(packageName: String) {
        repository.openAppInfo(packageName)
    }
}

data class SearchUiState(
        val query: String = "",
        val searchResults: List<AppModel> = emptyList(),
        val recentlyUsedApps: List<AppModel> = emptyList(),
        val isSearching: Boolean = false
)