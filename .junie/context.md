### Portal: The Browser Bar

**Portal** is a lightweight, efficient Android search bar designed for fast access to apps.

#### Core Features
- **App Search**: Fast app search with fuzzy matching.
- **"Top Hits" Prediction**: Based on time of day and location, the app predicts your most likely destination (e.g., Spotify in the morning, Uber at 5 PM).
- **Recent Tracking**: Recently used and recently installed app tracking.
- **Hidden Apps**: Manage and hide apps from the main launcher.
- **Lightweight Design**: Minimalist UI built for speed.

#### Tech Stack
- **Jetpack Compose**: For building a modern, reactive UI.
- **No DI libs**: Manual dependency injection.
- **Room Persistence Library**: Local database management for app tracking and state.
- **Coroutines & Flow**: Asynchronous programming and data streams.

#### Project Goals
The goal of this project is to provide a seamless experience for searching apps 
while maintaining a low memory footprint and high performance.
