# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build
./gradlew app:assembleDebug       # Debug APK
./gradlew app:bundleRelease       # Release AAB (for Play Store)

# Test
./gradlew test                    # All unit tests
./gradlew :domain:test            # Single module tests

# Code quality (required before pushing)
./gradlew spotlessApply           # Auto-format (ktlint)
./gradlew spotlessCheck           # Verify formatting
./gradlew check                   # Lint + unit tests
```

Spotless runs in ratchet mode from `origin/master`, so only changed files are checked on feature branches.

## Architecture

Four Gradle modules with a strict one-way dependency graph:

```
domain (pure JVM) ← data (Android) ← presentation (Android) ← app (Android)
```

**domain** — Business logic only. No Android dependencies. Use cases are `fun interface` with `suspend operator fun invoke()`. All errors flow as `Either<AppError, T>` (Arrow).

**data** — Implements domain use cases. Koin DI wires repositories, Room DAOs, Ktor HTTP services, and DataStore preferences. Never referenced directly by presentation.

**presentation** — Jetpack Compose UI. ViewModels use `StateFlow` with `SharingStarted.WhileSubscribed(500)`. Screens are stateless composables; state is hoisted via ViewModel. Navigation uses Navigation 3 with type-safe routes.

**app** — Entry point only. Initializes Koin and Firebase.

## Key Patterns

**Error handling** — `AppError` is a sealed class hierarchy (`AppError.Remote`, `AppError.Auth`, `AppError.Database`). Multiple errors use `AppError.Multi(NonEmptyList<AppError>)`. Use case return types are always `Either<AppError, T>`.

**Domain IDs** — Value classes with `@JvmInline`: `MangaId`, `ChapterId`, `UserId`. Never use raw strings/longs for entity IDs.

**Use case definition** (domain layer):
```kotlin
fun interface Login {
    suspend operator fun invoke(usernameOrEmail: String, password: String): Either<AppError, User>
}
```

**ViewModel pattern**:
```kotlin
val state: StateFlow<ScreenState> = screenStateFlow()
    .onStart { /* load data */ }
    .stateIn(defaultScope, SharingStarted.WhileSubscribed(500), ScreenState())
```

**DI** — Koin modules live in `data/di/` (repositories, use cases, network, database) and `presentation/di/` (ViewModels). No Hilt.

**Networking** — Ktor Client with OkHttp engine. Remote services are in `data/source/remote/`. Domain ↔ data model conversion uses explicit mapper functions.

## Tech Stack Versions

- Kotlin 2.3.21, JVM target 17
- Compose BOM 2026.04.01, Material3
- Ktor 3.4.3, Room 2.8.4, Koin 4.2.1, Arrow 2.2.2.1, Coil 3.4.0
- Min SDK 26, Target/Compile SDK 36

All dependency versions are managed in `gradle/libs.versions.toml`. Custom Gradle conventions live in `buildSrc/`.
