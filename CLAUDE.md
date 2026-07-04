# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug
./gradlew assembleRelease          # requires KEYSTORE_PATH, KEY_ALIAS, KEY_PASSWORD, STORE_PASSWORD env vars; falls back to debug signing if absent
./gradlew testDebugUnitTest        # JVM unit tests
./gradlew connectedDebugAndroidTest  # instrumented tests (device/emulator required)
./gradlew lint
```

## Architecture

**Portal** is a lightweight Android launcher/search bar for fast app access. Three-layer architecture: Data → Domain → UI.

### Dependency Injection
No DI framework. `PortalApplication` is the manual DI container — lazily initializes `AppDatabase`, `AppDao`, `IconStorage`, and `AppRepository`. `MainActivity` creates `ViewModelProvider.Factory` inline, pulling from `(application as PortalApplication).repository`. Do not use global singletons for business logic; inject through the application container.

### Data Layer
- `AppRepository` — single source of truth. Queries `PackageManager` for launchable apps, persists via Room, caches icons to `filesDir/app_icons/`. Uses `SharedPreferences` to track whether initial refresh has run.
- `AppDao` — exposes `Flow<List<AppEntity>>`. Recents query: `lastUsedTime DESC LIMIT 8`. Main listing: `usageCount DESC, label ASC` (hidden apps excluded).
- `IconStorage` — writes/reads app icons as PNG files.
- `PackageChangeReceiver` — `BroadcastReceiver` for package install/remove/change events; has its own `CoroutineScope(SupervisorJob() + Dispatchers.IO)`.

### Domain Layer
`AppModel` — plain data class, no framework dependencies. No use cases exist yet; `AppRepository` is called directly from ViewModels.

### UI Layer
Jetpack Compose + Material3. Navigation via `NavHost` with string routes `"search"` and `"settings"`.

**SearchViewModel** — exposes `uiState: MutableStateFlow<SearchUiState>` directly. Search pipeline: `debounce(50ms)` → fuzzy scorer (exact prefix = 100, substring = 50, sequential char match = 25 + density bonus, plus `usageCount * 0.1`). `AppFilter` enum: `RECENTS`, `ALL`, `GAMES`.

**Shortcut mode** — activated by pressing Space when the search bar is empty. Sets `SearchUiState.isShortcutMode = true`. While active, each app in the grid shows a letter badge (A, B, C…) in its top-left corner; typing that letter launches the corresponding app. Back button exits shortcut mode via `BackHandler`. `exitShortcutMode()`, `launchApp()`, and `resetToRecents()` all clear `isShortcutMode`.

**Pinned apps** — long-press context menu exposes "Pin"/"Unpin". Pinned apps are stored via `isPinned` on `AppEntity` (Room column, DB version 4). `AppDao.getPinnedApps()` returns a `Flow<List<AppEntity>>` ordered by label. `SearchUiState` holds a `pinnedApps` list; `displayApps` for the `RECENTS` filter prepends pinned apps before the 8 recent apps (duplicates excluded). Pinned apps show a primary-colored circle with a pin icon (`ic_pin.xml`) in the top-right corner of their grid tile. `SearchViewModel.togglePin()` flips the current state by checking `pinnedApps`.

**SettingsViewModel** — manages app visibility. "Recently installed" = apps installed within the last hour.

### Screens
- **SearchScreen**: `Scaffold` with `TextField` search bar (top) + `FilterChipsRow` (bottom, above keyboard). Shows `RecentAppsGrid` (4-column grid) when idle, `SearchResultsList` when searching. Long-press context menu: Hide / App Info / Play Store.
- **SettingsScreen**: `TopAppBar` + `LazyColumn`. Sections: recently installed (1h) and all apps, each with a visibility `Switch`.

## Conventions

**State:** ViewModels expose `MutableStateFlow` directly as a public property — no private backing field + public read-only `StateFlow`.

**Icons:** Use XML drawables in `res/drawable/`. Do not use `androidx.compose.material.icons.extended` (Material2 extended icons).

**Models:** `AppModel` is the domain object used throughout the UI layer. `AppEntity` stays within the data layer.
