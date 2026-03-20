package com.portal.browserbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.portal.browserbar.ui.search.SearchScreen
import com.portal.browserbar.ui.search.SearchViewModel
import com.portal.browserbar.ui.settings.SettingsScreen
import com.portal.browserbar.ui.settings.SettingsViewModel
import com.portal.browserbar.ui.theme.PortalTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
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
                            val viewModel: SearchViewModel = koinViewModel()
                            SearchScreen(
                                viewModel = viewModel,
                                onOpenSettings = { navController.navigate("settings") }
                            )
                            LaunchedEffect(Unit) {
                                reportFullyDrawn() // TODO place it at the right place
                            }
                        }
                        composable("settings") {
                            val viewModel: SettingsViewModel = koinViewModel()
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}