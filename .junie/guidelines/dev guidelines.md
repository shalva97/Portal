### State Management
- **No Backing Fields for State**: Do not use private `MutableStateFlow` or `MutableLiveData` with a public immutable `StateFlow`/`LiveData` (backing fields).
- **Public Mutable State**: Expose the `MutableStateFlow` directly as a public property in ViewModels.

### Small things
- Don't use androidx.compose.material.icons.extended from Material 2. instead, add XML icons

