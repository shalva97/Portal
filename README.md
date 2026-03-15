# Portal Browser Bar

A lightweight, high-performance Android launcher and search utility designed for speed and efficiency.

## Features

- **Fast Search**: Instant search for installed applications using a local Room database cache.
- **Usage Tracking**: Automatically tracks app usage to rank frequently used apps higher in search results.
- **App Management**: Easily hide apps from the search results or uninstall them directly.
- **Smart Indexing**:
    - **Broadcast Listener**: Automatically updates the app list when new apps are installed or uninstalled.
    - **Efficient Scans**: Implements a 30-minute cooldown period for OS scans to save battery and resources.
- **Modern Tech Stack**: Built with Jetpack Compose, Kotlin Coroutines, Room, and Koin.

## Getting Started

### Prerequisites
- Android Studio Ladybug or newer.
- Android SDK 24+.

### Building
1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the `app` module.

## Architecture

The project follows Clean Architecture principles with a focus on reactive programming:
- **UI**: Jetpack Compose for a modern, declarative UI.
- **DI**: Koin for lightweight dependency injection.
- **Data**: Room database for persistent storage and `PackageManager` for OS integration.
- **State Management**: Kotlin Flow and `ViewModel` for reactive UI state.

## License
MIT License
