# Manhwa Reader - Development Guidelines

Last verified against the repository on 2026-08-16. Treat the checked-in Gradle files and version catalog as the source of truth when these guidelines and the code diverge.

## Project Overview
This is a multi-module Android application for reading manga/manhwa built with modern Android development practices. The project follows Clean Architecture principles with separate modules for domain logic, data access, presentation, and the main application.

## Build Configuration

### Module Structure
- **app**: Main Android application module
- **domain**: Kotlin/JVM module containing business logic, use cases, and models; it has no project-module dependencies but does use external Kotlin, Arrow, serialization, coroutine, datetime, and immutable-collection libraries
- **data**: Data access layer with repositories and data sources
- **presentation**: UI layer with Compose screens, ViewModels, and UI components

### Build Requirements
- **Android API**: Min SDK 26, target SDK 37, compile SDK API 37.1
- **Java Version**: Java 21 via the Kotlin JVM toolchain
- **Kotlin**: 2.4.10 with the Compose compiler plugin
- **Android Gradle Plugin**: 9.3.1

### Key Dependencies
- **Jetpack Compose**: BOM 2026.08.00 for UI
- **Koin**: 4.2.2 for dependency injection
- **Coil**: BOM 3.5.0 for image loading
- **Room**: 2.8.4 for the local database
- **Ktor**: 3.5.2 for HTTP networking; Retrofit is not used
- **Coroutines**: 1.11.0 for asynchronous programming
- **KotlinX Serialization**: 1.11.0 for JSON handling
- **KotlinX Collections Immutable**: 0.5.1 for immutable data structures
- **Arrow**: 2.2.3 for typed error handling and functional utilities
- **Navigation 3**: Navigation 3 core `1.1.6` with Lifecycle ViewModel Navigation 3 `2.11.0`

### Build Variants
- **debug**: Development build with `.debug` application ID and `-debug` version suffix
- **release**: Signed release build with optimization enabled; signing values are read from `local.properties` when available
- There is no `staging` build type or flavor in the current application module, and no explicit `proguardFiles` or `minifyEnabled` declaration is present there

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
```text
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :domain:test
./gradlew :app:connectedAndroidTest

# Run a specific test class
./gradlew :presentation:test --tests "com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaViewDataTest"
```

### Test Example
The project includes a representative unit test in the presentation module:
- Location: `presentation/src/test/java/com/spiderbiggen/manga/presentation/ui/manga/list/model/MangaViewDataTest.kt`
- Uses JUnit 4 and tests manga view-data mapping behavior
- Follow the module build files for the test dependencies available in each source set

### Testing Guidelines
- Use descriptive test names with backticks, preferably in the form `` `given [precondition] when [action] then [expected result]` ``
- Test both success and error cases
- Use `assertEquals` for value comparisons
- Test edge cases and boundary conditions
- Mock external dependencies using appropriate mocking frameworks

## Code Style & Architecture

### Code Formatting
The project uses **Spotless** with **ktfmt 0.64** for automatic code formatting:

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
- Kotlin files formatted with ktfmt
- Gradle files (.gradle.kts) formatted with ktfmt
- Misc files (.gitignore, .properties, .md) formatted
- Ratchet mode: only formats files changed from `origin/master`

### Architectural Patterns

#### Domain Layer
- **Use Cases**: Implemented as functional interfaces with suspend operator invoke
```kotlin
fun interface GetManga {
    suspend operator fun invoke(id: MangaId): Either<AppError, Manga>
}
```

- **Error Handling**: Uses Arrow's `Either<Error, Success>` convention, with errors on the left and successful values on the right
- **Type Safety**: Value classes for IDs with `@JvmInline` and `@Serializable`
```kotlin
@JvmInline
@Serializable
value class MangaId(val value: String)
```

#### Presentation Layer
- **ViewModels**: Use constructor parameters and Koin modules for dependency injection; do not use Hilt annotations
- **State Management**: `StateFlow` for UI state, `SharedFlow` for events
- **Lifecycle Awareness**: `SharingStarted.WhileSubscribed(500)` for proper lifecycle handling
- **Immutable Collections**: Use `kotlinx-collections-immutable` for UI state
- **Navigation**: Uses AndroidX Navigation 3 APIs with Koin's Navigation 3 integration; pass navigation parameters through the established Koin/ViewModel patterns

#### Data Layer
- **Repository Pattern**: Separate repositories for different data concerns
- **Dependency Injection**: Koin modules for providing repositories, data sources, HTTP clients, and other dependencies

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
- Use Arrow's `Either` for operations that can fail, keeping the error type on the left
- Use the Arrow APIs and project extensions established around `Either` for safe value extraction
- Log errors appropriately with meaningful messages

### Dependencies & Injection
- Use constructor parameters and declare providers in Koin modules
- Prefer interfaces over concrete implementations
- Use Koin scopes deliberately; do not introduce Hilt alongside Koin

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
3. Generate and inspect coverage: `./gradlew coverage`
4. Ensure build passes: `./gradlew build` (if necessary)

### Coverage
- Kover `0.9.9` is the project coverage engine; `./gradlew coverage` runs host unit tests and generates the aggregate XML and HTML reports.
- Reports are written to `build/reports/coverage/coverage.xml` and `build/reports/coverage/html/index.html`.
- Generated classes and Compose previews are excluded, together with the narrow `presentation/framework/adapter` Sonar boundary for passive Compose or Android system integrations.
- `com.spiderbiggen.manga.presentation.coverage.CoverageExcluded` is an explicit reviewed marker for adapter declarations only; never mark ViewModels, state mapping, repositories, domain logic, or behavioral UI code, and extract and test pure decisions before excluding a wrapper.
- SonarCloud consumes the aggregate XML report and owns the 80% new-code quality gate.

### Commit and Pull Request Messages
- Use the Conventional Commits format for every commit subject and pull request title: `<type>(<optional-scope>): <imperative description>`.
- Allowed types are `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, and `revert`; append `!` before the colon for breaking changes.
- Allowed lowercase scopes are `app`, `data`, `domain`, `presentation`, `build`, `repo`, `deps`, `ci`, `release`, `docs`, and `auth`.
- Subjects must begin with lowercase text, use imperative wording, omit a trailing period, and have no length limit; temporary `fixup!` and `squash!` commits are allowed locally.
- Pull request descriptions must contain exactly `## Summary` followed by `## Changes`, with at least one `-` bullet under each and no verification or testing section.
- Activate the versioned commit hook in a clone with `git config core.hooksPath .githooks`.
- The pull request template in `.github/pull_request_template.md` is the canonical description structure, and `.github/workflows/message_check.yaml` enforces these rules for pull requests.

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
- **domain**: No project-module dependencies; external Kotlin, Arrow, serialization, coroutine, datetime, and immutable-collection dependencies are declared in its Gradle file

This dependency structure ensures proper separation of concerns and prevents circular dependencies.
