### Android Architecture Overview

This project follows the **Google Recommended Android Architecture**, ensuring a clear separation of concerns, scalability, and testability.

#### 1. UI Layer
- **Composables**: All UI components are built using **Jetpack Compose**.
- **ViewModels**: Manage UI state and provide access to business logic.
    - **Rule**: Expose state using `MutableStateFlow` directly (no backing fields, as per project guidelines).
    - ViewModels do not have direct knowledge of the UI (Composables).

#### 2. Domain Layer (Optional)
- *Note: In its current state, the project uses a simplified approach where Repositories are accessed directly by ViewModels.*
- As complexity grows, UseCases will be introduced to encapsulate specific pieces of business logic.

#### 3. Data Layer
- **Repositories**: Serve as the single point of truth for data. They coordinate between different data sources (Local and Remote).
    - Example: `AppRepository`.
- **Data Sources**:
    - **Local**: Managed by **Room Persistence Library** (`AppDatabase`, `AppDao`).
    - **Remote**: (If applicable) API clients using Retrofit/Ktor.
- **Models**: Clean separation between `Entity` (Database), `Dto` (Network), and `DomainModel` (UI).

#### Data Flow
- **UDF (Unidirectional Data Flow)**: State flows down from the Data Layer to the UI Layer, and events flow up from the UI Layer to the Data Layer.
- **Reactive Stream**: Data is observed from the Database through the Repository and exposed to the UI via `StateFlow`.
