### Dependency Injection (DI)

Currently, this project uses **Manual Dependency Injection** to manage dependencies and provide them to the UI layer.

#### 1. Current Implementation (Manual DI)

- **PortalApplication**: Acts as a central dependency container. It initializes core components such as the `AppDatabase`, `AppDao`, and `AppRepository`.
- **Repositories**: Injected manually into ViewModels via their constructors.
- **ViewModels**: Created using a `ViewModelProvider.Factory` in `MainActivity`. The factory retrieves the `AppRepository` from the `PortalApplication` and passes it to the ViewModel.

#### 2. Key Components

- **Application Container**: `com.portal.browserbar.PortalApplication`
- **ViewModel Factory**: `com.portal.browserbar.MainActivity` (manual factory implementation)

#### 3. Guidelines for Dependencies

- **Avoid Global State**: Do not use global singleton objects for business logic; prefer injecting them through the application container.
