### Project Screens

This document describes the screens available in the **Portal: The Browser Bar** application.

#### 1. Search Screen (`SearchScreen.kt`)
The main entry point of the application, designed for quick access to apps and web search.

- **SearchBar**: A top-mounted search bar for entering app names or web queries. It includes a settings icon to navigate to the settings screen.
- **Recent Apps Grid**: Displays a grid of recently used apps when the search query is empty. This allows for one-tap access to frequent destinations.
- **Search Results List**: As the user types, a list of matching apps is shown, sorted by a fuzzy matching score and usage frequency.
- **App Interactions**:
    - **Single Tap**: Launches the selected app and increments its usage count.
    - **Long Press**: Opens a context menu providing options to hide the app, view app info, or open it in the Play Store.

#### 2. Settings Screen (`SettingsScreen.kt`)
Allows users to manage the behavior and visibility of apps within the launcher.

- **Top Bar**: Includes a title ("Settings") and a back button to return to the search screen.
- **Recently Installed Apps**: A section listing apps installed within the last hour, making it easy to manage new additions.
- **App Info**: Displays the app icon, label, and package name for easy identification.
