package com.portal.browserbar.ui.settings

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.portal.browserbar.R
import com.portal.browserbar.domain.model.AppModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (uiState.recentlyInstalledApps.isNotEmpty()) {
                item {
                    Text(
                        "Recently Installed (1h)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(uiState.recentlyInstalledApps) { app ->
                    AppSettingsItem(app, viewModel)
                }
            }

            item {
                Text(
                    "All Apps",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(uiState.allApps) { app ->
                AppSettingsItem(app, viewModel)
            }
        }
    }
}

@Composable
fun AppSettingsItem(app: AppModel, viewModel: SettingsViewModel) {
    ListItem(
        headlineContent = { Text(app.label) },
        supportingContent = { Text(app.packageName) },
        leadingContent = {
            app.iconPath?.let {
                BitmapFactory.decodeFile(it)?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        trailingContent = {
            Switch(
                checked = !app.isHidden,
                onCheckedChange = { viewModel.toggleAppVisibility(app.packageName, !it) }
            )
        }
    )
}
