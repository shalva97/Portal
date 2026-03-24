package com.portal.browserbar.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.portal.browserbar.R
import com.portal.browserbar.domain.model.AppModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            SearchBar(
                    query = uiState.query,
                    onQueryChanged = viewModel::onQueryChanged,
                    onSettingsClick = onOpenSettings,
                    onDone = {
                        uiState.searchResults.firstOrNull()?.let { viewModel.launchApp(it) }
                    },
                    focusRequester = focusRequester,
                    modifier = Modifier.fillMaxWidth()
                            .statusBarsPadding()
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (uiState.isSearching) {
                SearchResultsList(
                    results = uiState.searchResults,
                    onAppClick = viewModel::launchApp,
                    onAppLongClick = { /* Show menu */ },
                    viewModel = viewModel
                )
            } else {
                RecentAppsGrid(
                    apps = uiState.recentlyUsedApps,
                    onAppClick = viewModel::launchApp,
                    onAppLongClick = { /* Show menu */ },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onDone: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
            modifier = modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
        placeholder = { Text("Search apps...") },
        trailingIcon = {
            IconButton(onClick = onSettingsClick) {
                Icon(painterResource(R.drawable.settings), contentDescription = "Settings")
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}

@Composable
fun RecentAppsGrid(
    apps: List<AppModel>,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    viewModel: SearchViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(apps) { app ->
            AppGridItem(app, onClick = { onAppClick(app) }, onLongClick = { onAppLongClick(app) }, viewModel = viewModel)
        }
    }
}

@Composable
fun SearchResultsList(
    results: List<AppModel>,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    viewModel: SearchViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(results) { app ->
            AppListItem(app, onClick = { onAppClick(app) }, onLongClick = { onAppLongClick(app) }, viewModel = viewModel)
        }
    }
}

@Composable
fun AppGridItem(app: AppModel, onClick: () -> Unit, onLongClick: () -> Unit, viewModel: SearchViewModel) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
                .padding(8.dp)
                .clickable { onClick() }
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        app.icon?.let {
            Image(
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        
        // Simplified long press handling for this example
        // In a real app, use pointerInput for proper long press
        Box {
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Hide") }, onClick = { viewModel.hideApp(app.packageName); showMenu = false })
                DropdownMenuItem(text = { Text("Uninstall") }, onClick = { viewModel.uninstallApp(app.packageName); showMenu = false })
                DropdownMenuItem(text = { Text("Play Store") }, onClick = { viewModel.openInPlayStore(app.packageName); showMenu = false })
            }
        }
    }
}

@Composable
fun AppListItem(app: AppModel, onClick: () -> Unit, onLongClick: () -> Unit, viewModel: SearchViewModel) {
    ListItem(
        headlineContent = { Text(app.label) },
        supportingContent = { Text(app.packageName) },
        leadingContent = {
            app.icon?.let {
                Image(
                    bitmap = it.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        modifier = Modifier.clickable { onClick() }
    )
}
