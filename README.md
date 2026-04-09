# Run The World

A gamified urban running and territory-claiming app built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP).

## Architecture

```
RunTheWorld/
├── shared/                         # KMP module — pure business logic
│   └── src/
│       ├── commonMain/kotlin/com/runtheworld/
│       │   ├── domain/
│       │   │   ├── model/          # GpsPoint, Run, Territory, UserProfile
│       │   │   └── repository/     # Repository interfaces
│       │   ├── data/
│       │   │   ├── local/db/       # Room 2.7 KMP — entities, DAOs, AppDatabase
│       │   │   └── repository/     # Repository implementations
│       │   ├── presentation/       # ViewModels (ProfileViewModel, RunViewModel, MapViewModel, HistoryViewModel)
│       │   ├── platform/           # LocationService interface
│       │   ├── util/               # AppResult, ConvexHull, DistanceCalculator
│       │   └── di/                 # Koin modules (platformModule expect + shared modules)
│       ├── androidMain/            # Android Room driver, AndroidLocationService, Koin platform module
│       └── iosMain/                # iOS Room driver, IosLocationService, Koin platform module
│
├── app/                            # Compose Multiplatform UI module
│   └── src/
│       ├── commonMain/kotlin/com/runtheworld/
│       │   ├── App.kt              # Root composable — checks profile, routes to map
│       │   ├── navigation/         # NavGraph with NavHost
│       │   └── ui/
│       │       ├── theme/          # MaterialTheme, color tokens, hex parser
│       │       ├── profile/        # ProfileSetupScreen (first-launch username + colour)
│       │       ├── map/            # MapScreen + RunTheWorldMap (expect)
│       │       ├── run/            # RunScreen (live GPS + stop button)
│       │       └── history/        # HistoryScreen (past runs + profile stats)
│       ├── androidMain/            # RunTheWorldMap actual (Google Maps Compose)
│       │                           # MainActivity, RunTheWorldApplication
│       └── iosMain/                # RunTheWorldMap actual (MapKit via UIKitView)
│                                   # MainViewController
│
└── iosApp/                         # Xcode project — SwiftUI wrapper
    └── iosApp/
        ├── iOSApp.swift
        ├── ContentView.swift       # Wraps MainViewController via UIViewControllerRepresentable
        └── Info.plist
```

## Setup

### 1. Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Narwhal (2025.1) or newer (required for AGP 9.x) |
| JDK | 17+ |
| Xcode | 15+ (macOS only, for iOS builds) |
| Kotlin Multiplatform plugin | latest |

---

### 2. Clone and open

```bash
git clone <your-repo>
cd RunTheWorld
```

Open the root `RunTheWorld/` folder in Android Studio. Sync Gradle.

---

### 3. Google Maps API key (Android)

1. Go to [Google Cloud Console](https://console.cloud.google.com/) → APIs & Services → Credentials
2. Create an **API key** and enable **Maps SDK for Android**
3. Add to `local.properties` (this file is gitignored):

```properties
MAPS_API_KEY=YOUR_KEY_HERE
```

The key is injected automatically into `AndroidManifest.xml` via `manifestPlaceholders` in `app/build.gradle.kts`.

---

### 4. Run on Android

In Android Studio, select the `:app` run configuration and choose an Android device/emulator. The app will:
- Open a profile setup screen on first launch
- Show a full-screen Google Maps view after setup
- Track GPS when you tap the run button

---

### 5. Run on iOS (requires macOS + Xcode)

```bash
# Build the shared + app frameworks for the iOS simulator
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
./gradlew :app:linkDebugFrameworkIosSimulatorArm64
```

Then open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator.

> **First time:** You need to generate the Xcode project. Use the [KMP Wizard](https://kmp.jetbrains.com/) or run:
> ```bash
> ./gradlew generateXcodeProject
> ```
> If your project doesn't include that task, create the `.xcodeproj` manually by running the app module's `embedAndSignAppleFrameworkForXcode` Gradle task and linking the output framework.

**MapKit key:** No API key needed — MapKit is free with an Apple Developer account and an entry in `Info.plist` (already done).

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Room 2.7 KMP** | Single Room database definition in `commonMain`; Android uses the standard SQLite driver, iOS uses the bundled SQLite driver from `sqlite-bundled`. |
| **Compose Multiplatform** | Single UI codebase targeting Android and iOS. Windows-compatible for full Android dev. |
| **expect/actual MapView** | Maps are too platform-specific for a shared composable. `RunTheWorldMap` is an `expect` composable with Google Maps on Android and MapKit on iOS. |
| **Koin `expect val platformModule`** | Each platform supplies its own Koin module (Room driver, LocationService, Settings). `commonMain` wires the rest. |
| **Offline-first** | No networking. All runs and territories stored in Room. Ready for a backend layer — just add a `RemoteDataSource` to each repository. |
| **ConvexHull (Graham Scan)** | Pure Kotlin implementation in `commonMain`. Computes the polygon claimed after each run from the GPS path. |

---

## Adding a Backend Later

The repository interfaces are already defined and decoupled from the data source. To add a backend:

1. Add Ktor to `shared/build.gradle.kts`
2. Create DTO classes and a `RunTheWorldApi` Ktor client
3. Add remote data source calls to each `*RepositoryImpl` (fallback to local on network error)
4. Add your backend base URL to a config object

The proposed JSON contracts are in the project docs.

---

## Dependency Versions

See `gradle/libs.versions.toml` for all pinned versions. Key ones:

| Library | Version | Notes |
|---------|---------|-------|
| Kotlin | 2.1.0 | |
| AGP | 9.0.1 | Requires Android Studio Narwhal |
| Compose Multiplatform | 1.7.3 | |
| Room | 2.7.0-rc01 | First KMP-compatible Room release |
| Koin | 4.0.0 | |
| navigation-compose | 2.8.0-alpha10 | JetBrains CMP fork |

> If `navigation-compose` 2.8.0-alpha10 is unavailable, check the latest at
> `https://maven.pkg.jetbrains.space/public/p/compose/dev`.
