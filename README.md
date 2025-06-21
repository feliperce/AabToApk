![Kotlin Badge](https://img.shields.io/badge/kotlin-v2.1.10-%237F52FF?logo=kotlin)
![Compose Badge](https://img.shields.io/badge/compose_multiplatform-v1.8.0-%234285F4?logo=jetpackcompose)
![Platform Windows Badge](https://img.shields.io/badge/platform-windows-%230078D4?logo=windows)
![Platform Linux Badge](https://img.shields.io/badge/platform-Linux-%23FCC624?logo=linux)
![Platform MacOS Badge](https://img.shields.io/badge/platform-macOS-%23000000?logo=macos)
![Android Badge](https://img.shields.io/badge/platform-Android-%2334A853?logo=android)
![WebAssembly](https://img.shields.io/badge/platform-WebAssembly-23000000?logo=webassembly&color=%23654FF0)
![Ktor Server](https://img.shields.io/badge/platform-Ktor_server-23000000?logo=ktor&color=%23087CFA)

# AabToApk - Extract .apk from .aab

AabToApk is a powerful, cross-platform tool that allows developers to extract APK files from Android App Bundles (AAB). Built with Kotlin Multiplatform, it provides a consistent experience across Desktop, Android, Web, and Server platforms.

<p align="center">
  <img src="https://github.com/user-attachments/assets/25b4e1a1-daeb-42f8-8f4d-8f8fec949845" width=50% height=50%>
</p>

## Features

- Extract APK files from AAB bundles
- Support for multiple platforms (Windows, macOS, Linux, Android, Web)
- Local extraction on Desktop and Android
- Remote extraction via Web interface
- User-friendly interface with Material 3 design
- Keystore management for signing APKs

## Platforms

### Desktop (Windows, macOS, Linux)
- Native application with local extraction capabilities
- 100% code sharing with Android version
- Available as native installers (MSI, DMG, DEB)

### Android
- Full-featured mobile application
- Local extraction without server dependency

### Web (WebAssembly)
- Browser-based interface
- Uses server API for extraction

### Server
- Ktor-based backend service
- Handles extraction requests from web clients
- Manages extraction jobs with Quartz

## Development

### Prerequisites
- JDK 11 or higher
- Android SDK
- PostgreSQL (for server component)

### Project Structure
- **composeApp**: Main application module for Android and Desktop platforms
- **remoteApp**: Web application module using WebAssembly
- **server**: Ktor server module
- **extractor**: Core functionality for extracting APK from AAB
- **sharedRemote**: Shared code between server and remote app
- **sharedui**: Shared UI components

### Building and Running

#### Desktop Application
```bash
./gradlew :composeApp:run
```

#### Android Application
```bash
./gradlew :composeApp:installDebug
```

#### Server
```bash
./gradlew :server:run
```

#### Web (WASM) Application
```bash
./gradlew :remoteApp:wasmJsBrowserDevelopmentRun
```

## Architecture
- Multi-module architecture with shared code between platforms
- MVI (Model-View-Intent) pattern for UI state management
- Dependency injection with Koin
- Database access with Room (local) and Exposed (server)

## Screenshots

### Desktop
<p align="center">
  <img src="https://github.com/user-attachments/assets/ac74ff94-1fa5-4dc9-8d0d-29fe0aef11d7" width=50% height=50%>
</p>

### Android
<p align="center">
  <img src="https://github.com/user-attachments/assets/278cb8ff-8a69-4eb1-87dc-471776201b02" width=50% height=50%>
</p>

### Web (WASM)
<p align="center">
  <img src="https://github.com/user-attachments/assets/bf149c07-32c2-4401-9b53-bcc9f3dda938" width=50% height=50%>
</p>

## License
This project is licensed under the MIT License - see the LICENSE file for details.
