![Kotlin Badge](https://img.shields.io/badge/kotlin-v2.0.0-%237F52FF?logo=kotlin)
![Compose Badge](https://img.shields.io/badge/compose_multiplatform-v1.6.10-%234285F4?logo=jetpackcompose)
![Platform Windows Badge](https://img.shields.io/badge/platform-windows-%230078D4?logo=windows)
![Platform Linux Badge](https://img.shields.io/badge/platform-Linux-%23FCC624?logo=linux)
![Platform MacOS Badge](https://img.shields.io/badge/platform-macOS-%23000000?logo=macos)
![Android Badge](https://img.shields.io/badge/platform-Android-%2334A853?logo=android)
![WebAssembly](https://img.shields.io/badge/platform-WebAssembly-23000000?logo=webassembly&color=%23654FF0)
![Ktor Server](https://img.shields.io/badge/platform-Ktor_server-23000000?logo=ktor&color=%23087CFA)


# AabToApk - Extract .apk from .aab

This is a Kotlin Multiplatform project targeting Android, Desktop, Web and Server.

### Local - Android and Desktop
Running local extractor, 100% code share.

Built with:
* Kotlin Multiplatform
* Compose Multiplatform
* Material 3
* Room
* DataStore
* Koin
* MVI

### Remote - WASM
Using api from ktor server to extract .apk

Built with:
 * Kotlin Multiplatform
* Compose Multiplatform
* Material 3
* Ktor client
* MVI

### Server
Ktor server that WASM consumes

Built with:
* Kotlin Multiplatform
* Ktor Server
* Material 3
* Exposed (Postgres)
* Quartz for jobs

## Screenshots

Desktop:

<p align="center">
  <img src="https://github.com/user-attachments/assets/ac74ff94-1fa5-4dc9-8d0d-29fe0aef11d7" width=50% height=50%>
</p>

Android:

<p align="center">
  <img src="https://github.com/user-attachments/assets/278cb8ff-8a69-4eb1-87dc-471776201b02" width=50% height=50%>
</p>

Web (Wasm):

<p align="center">
  <img src="https://github.com/user-attachments/assets/bf149c07-32c2-4401-9b53-bcc9f3dda938" width=50% height=50%>
</p>





