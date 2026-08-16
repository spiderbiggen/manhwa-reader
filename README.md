# Manga Reader

An Android app for reading manga and manhwa without ads from multiple sources.

## Project structure

The project is organized into four Gradle modules:

- `app`: Android application and app-level configuration
- `domain`: Kotlin/JVM business logic, use cases, and models
- `data`: repositories, remote data sources, and local persistence
- `presentation`: Jetpack Compose UI, ViewModels, and navigation

The modules follow a Clean Architecture-style dependency direction: `app` depends on `data`, `domain`, and `presentation`; `data` and `presentation` depend on `domain`.

## Technology

- Kotlin `2.4.10` and Android Gradle Plugin `9.3.1`
- Jetpack Compose with BOM `2026.08.00`
- Koin for dependency injection
- Ktor for HTTP networking
- Room for local persistence
- Coil for image loading
- Kotlin coroutines and Flow for asynchronous work
- Arrow `Either` for typed error handling

## Requirements

- JDK `21`
- Android SDK platform API `37` with extension level `1`
- Android SDK Build Tools and platform tools installed through Android Studio or the Android SDK manager

The Gradle wrapper uses Gradle `9.7.0`.

## Build and test

Use the Gradle wrapper from the project root:

```text
# macOS/Linux
./gradlew :app:assembleDebug
./gradlew test
./gradlew spotlessCheck

# Windows
gradlew.bat :app:assembleDebug
gradlew.bat test
gradlew.bat spotlessCheck
```

The debug build uses the `.debug` application ID suffix and `-debug` version suffix. Release signing values are read from `local.properties` when configured; do not commit signing credentials or generated build artifacts.
