package com.portal.browserbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.portal.browserbar.ui.search.SearchScreen
import com.portal.browserbar.ui.search.SearchViewModel
import com.portal.browserbar.ui.settings.SettingsScreen
import com.portal.browserbar.ui.settings.SettingsViewModel
import com.portal.browserbar.ui.theme.PortalTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy { (application as PortalApplication).repository }

    private val searchViewModel: SearchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(repository) as T
            }
        }
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "search") {
                        composable("search") {
                            SearchScreen(
                                viewModel = searchViewModel,
                                onOpenSettings = { navController.navigate("settings") }
                            )
                            LaunchedEffect(Unit) {
                                searchViewModel.onUIFullyLoaded.collect { isFullyLoaded ->
                                    if (isFullyLoaded) {
                                        reportFullyDrawn()
                                    }
                                }
                            }
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}