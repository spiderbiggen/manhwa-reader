# Manhwa Reader - Development Guidelines

## Project Overview
This is a multi-module Android application for reading manga/manhwa built with modern Android development practices. The project follows Clean Architecture principles with separate modules for domain logic, data access, presentation, and the main application.

## Build Configuration

### Module Structure
- **app**: Main Android application module
- **domain**: Pure Kotlin module containing business logic, use cases, and models
- **data**: Data access layer with repositories and data sources
- **presentation**: UI layer with Compose screens, ViewModels, and UI components

### Build Requirements
- **Android API**: Min SDK 26, Target/Compile SDK 36
- **Java Version**: Java 17 (JVM target and source/target compatibility)
- **Kotlin**: 2.2.0 with Compose compiler plugin
- **Android Gradle Plugin**: 8.11.1

### Key Dependencies
- **Jetpack Compose**: BOM 2025.07.00 for UI
- **Dagger Hilt**: 2.57 for dependency injection
- **Coil**: 3.2.0 for image loading
- **Room**: 2.7.2 for local database
- **Retrofit**: 3.0.0 for networking
- **Coroutines**: 1.10.2 for asynchronous programming
- **KotlinX Serialization**: For JSON handling
- **KotlinX Collections Immutable**: 0.4.0 for immutable data structures

### Build Variants
- **debug**: Development build with debugging enabled, `.debug` suffix
- **staging**: Pre-production build with release optimizations, `.staging` suffix
- **release**: Production build with ProGuard, code shrinking, and signing

### Version Catalog
The project uses Gradle version catalogs (`gradle/libs.versions.toml`) for centralized dependency management. All dependencies are declared in the catalog and referenced using `libs.` notation.

### Custom Plugins
- **manga.spotless**: Custom Spotless configuration for code formatting (see Code Style section)

## Testing

### Test Setup
Testing is configured per module. To add testing to a module:

1. Add test dependencies to the module's `build.gradle.kts`:
```kotlin
dependencies {
    // For unit tests
    testImplementation(libs.junit)
    
    // For Android instrumented tests (app/presentation modules)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidX.compose.bom))
    androidTestImplementation(libs.androidX.compose.uiTestJunit4)
}
```

2. Create test directory structure:
```
src/test/java/com/spiderbiggen/manga/[module]/[package]/
src/androidTest/java/com/spiderbiggen/manga/[module]/[package]/  # For Android tests
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :domain:test
./gradlew :app:connectedAndroidTest

# Run specific test class
./gradlew :domain:test --tests "com.spiderbiggen.manga.domain.model.EitherTest"
```

### Test Example
The project includes a comprehensive test example for the `Either` class in the domain module:
- Location: `domain/src/test/java/com/spiderbiggen/manga/domain/model/EitherTest.kt`
- Tests functional programming utilities like `mapLeft`, `mapRight`, `leftOr`, `rightOr`
- Demonstrates proper test structure and naming conventions

### Testing Guidelines
- Use descriptive test names with backticks: `` `should return expected result when condition` ``
- Test both success and error cases
- Use `assertEquals` for value comparisons
- Test edge cases and boundary conditions
- Mock external dependencies using appropriate mocking frameworks

## Code Style & Architecture

### Code Formatting
The project uses **Spotless** with **ktlint 1.3.1** for automatic code formatting:

```bash
# Check formatting
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply
```

**Formatting Rules:**
- 4 spaces for indentation (tabs converted to spaces)
- Trailing whitespace trimmed
- Files end with newline
- Kotlin files formatted with ktlint
- Gradle files (.gradle.kts) formatted with ktlint
- Misc files (.gitignore, .properties, .md) formatted
- Ratchet mode: only formats files changed from `origin/master`

### Architectural Patterns

#### Domain Layer
- **Use Cases**: Implemented as functional interfaces with suspend operator invoke
```kotlin
fun interface GetManga {
    suspend operator fun invoke(id: MangaId): Either<Manga, AppError>
}
```

- **Error Handling**: Uses `Either<Success, Error>` monad pattern
- **Type Safety**: Value classes for IDs with `@JvmInline` and `@Serializable`
```kotlin
@JvmInline
@Serializable
value class MangaId(val inner: String)
```

#### Presentation Layer
- **ViewModels**: Use `@HiltViewModel` with dependency injection
- **State Management**: `StateFlow` for UI state, `SharedFlow` for events
- **Lifecycle Awareness**: `SharingStarted.WhileSubscribed(5_000)` for proper lifecycle handling
- **Immutable Collections**: Use `kotlinx-collections-immutable` for UI state
- **Navigation**: Type-safe navigation with `savedStateHandle.toRoute<>()`

#### Data Layer
- **Repository Pattern**: Separate repositories for different data concerns
- **Dependency Injection**: Hilt modules for providing dependencies

### Coding Conventions

#### Naming
- Use descriptive names for variables, functions, and classes
- Use camelCase for functions and variables
- Use PascalCase for classes and interfaces
- Use SCREAMING_SNAKE_CASE for constants

#### Coroutines
- Use appropriate dispatchers: `Dispatchers.Main` for UI, `Dispatchers.Default` for CPU work
- Proper scope management with `viewModelScope` and custom scopes
- Use `yield()` for cooperative cancellation in long-running operations

#### State Management
- Emit immutable state objects
- Use `compareAndSet` for atomic state updates
- Collect flows with `collectLatest` for latest value semantics

#### Error Handling
- Use `Either` monad for operations that can fail
- Use extension functions like `leftOr()`, `leftOrElse()` for safe value extraction
- Log errors appropriately with meaningful messages

### Dependencies & Injection
- Use constructor injection with `@Inject`
- Prefer interfaces over concrete implementations
- Use `@HiltViewModel` for ViewModels
- Use `@Singleton` sparingly, prefer scoped instances

### UI Development
- Use Jetpack Compose for all UI
- Prefer stateless composables
- Use `remember` for expensive computations
- Use proper preview annotations for Compose previews
- Handle configuration changes properly with state hoisting

## Development Workflow

### Before Committing
1. Run code formatting: `./gradlew spotlessApply`
2. Run tests: `./gradlew test`
3. Ensure build passes: `./gradlew build` (if necessary)

### Adding New Features
1. Start with domain models and use cases
2. Add repository interfaces in domain, implementations in data
3. Create ViewModels in presentation layer
4. Build UI with Compose
5. Add comprehensive tests
6. Update documentation if needed

### Module Dependencies
- **app** depends on: data, domain, presentation
- **presentation** depends on: domain
- **data** depends on: domain
- **domain**: No dependencies (pure Kotlin)

This dependency structure ensures proper separation of concerns and prevents circular dependencies.
