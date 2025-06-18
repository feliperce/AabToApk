# AabToApk Development Guidelines

This document provides essential information for developers working on the AabToApk project.

## Build and Configuration Instructions

### Prerequisites
- JDK 11 or higher
- Android SDK
- PostgreSQL (for server component)

### Configuration

#### Local Properties
The project requires a `local.properties` file in the root directory with the following properties:

```properties
# Android SDK location
sdk.dir=/path/to/android/sdk

# Database configuration (for server)
sv.dbUser=postgres
sv.dbPassword=postgres

# Authentication token for API access
auth.token=your_auth_token

# Server host and port
sv.host=localhost
sv.port=8080

# Proxy configuration (if needed)
proxy.host=localhost
```

### Building and Running

#### Desktop Application
To build and run the desktop application:

```bash
./gradlew :composeApp:run
```

To create native distributions (Windows MSI, macOS DMG, Linux DEB):

```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

#### Android Application
To build and install the Android application:

```bash
./gradlew :composeApp:installDebug
```

#### Server
To run the server component:

```bash
./gradlew :server:run
```

The server requires a PostgreSQL database with credentials specified in `local.properties`.

#### Web (WASM) Application
To build and run the web application:

```bash
./gradlew :remoteApp:wasmJsBrowserDevelopmentRun
```

## Project Structure

- **composeApp**: Main application module for Android and Desktop platforms
- **remoteApp**: Web application module using WebAssembly
- **server**: Ktor server module
- **extractor**: Core functionality for extracting APK from AAB
- **sharedRemote**: Shared code between server and remote app
- **sharedui**: Shared UI components

## Development Information

### Architecture
- The project follows a multi-module architecture with shared code between platforms
- Uses MVI (Model-View-Intent) pattern for UI state management
- Dependency injection with Koin
- Database access with Room (local) and Exposed (server)

### Code Style
- Kotlin Multiplatform conventions
- Compose UI components follow Material 3 design guidelines

### Important Implementation Details
- The desktop application uses JVM 11 and requires the "jdk.unsupported" module
- The Linux build additionally requires the "jdk.security.auth" module
- The server component reads database credentials and authentication token from local.properties
- Room database schema is stored in composeApp/schemas directory

### Deployment
- Desktop applications are packaged as native installers (MSI, DMG, DEB)
- Server can be deployed as a standalone application or in a container