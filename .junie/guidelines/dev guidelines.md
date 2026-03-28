### State Management
- **No Backing Fields for State**: Do not use private `MutableStateFlow` or `MutableLiveData` with a public immutable `StateFlow`/`LiveData` (backing fields).
- **Public Mutable State**: Expose the `MutableStateFlow` directly as a public property in ViewModels.
- **Rationale**: This simplifies the code and reduces boilerplate, making the codebase easier to maintain and reason about.
- **Example**:
  ```kotlin
  // Preferred
  val uiState = MutableStateFlow(MyUiState())

  // Avoid
  private val _uiState = MutableStateFlow(MyUiState())
  val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
  ```
