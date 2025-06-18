import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "2.0.20"
    id("com.github.gmazzo.buildconfig") version "5.5.0"
}

val host: String = gradleLocalProperties(rootDir).getProperty("sv.host") ?: ""
val port: String = gradleLocalProperties(rootDir).getProperty("sv.port") ?: ""
val proxyHost: String = gradleLocalProperties(rootDir).getProperty("proxy.host") ?: ""
val maxAabUploadMb: String = System.getenv("AAB_MAX_UPLOAD_MB") ?: "100"
val removeUploadHourTime: String = System.getenv("AAB_REMOVE_UPLOAD_HOUR_TIME") ?: "1"
val cachePath: String = System.getenv("AAB_CACHE_PATH") ?: "/tmp/AabToApk"
val buildToolsPath: String = System.getenv("AAB_BUILD_TOOLS_PATH") ?: ""
val keystorePath: String = System.getenv("DEBUG_KEYSTORE_PATH") ?: ""
val keystoreAlias: String = System.getenv("DEBUG_KEYSTORE_ALIAS") ?: "androiddebugkey"
val keystoreStorePassword: String = System.getenv("DEBUG_KEYSTORE_STORE_PASSWORD") ?: "android"
val keystoreKeyPassword: String = System.getenv("DEBUG_KEYSTORE_KEY_PASSWORD") ?: "android"

buildConfig {
    buildConfigField("HOST", host)
    buildConfigField("PORT", port)
    buildConfigField("PROXY_HOST", proxyHost)
    buildConfigField("MAX_AAB_UPLOAD_MB", maxAabUploadMb)
    buildConfigField("REMOVE_UPLOAD_HOUR_TIME", removeUploadHourTime)
    buildConfigField("CACHE_PATH", cachePath)
    buildConfigField("BUILD_TOOLS_PATH", buildToolsPath)
    buildConfigField("DEBUG_KEYSTORE_PATH", keystorePath)
    buildConfigField("DEBUG_KEYSTORE_ALIAS", keystoreAlias)
    buildConfigField("DEBUG_KEYSTORE_STORE_PASSWORD", keystoreStorePassword)
    buildConfigField("DEBUG_KEYSTORE_KEY_PASSWORD", keystoreKeyPassword)
}

kotlin {

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "remoteApp"
        browser {
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "remoteApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.serialization.json)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)

            implementation(libs.bundletool)

            implementation(libs.kotlinx.datetime)
        }
    }
}

android {
    namespace = "io.github.feliperce.aabtoapk.sharedRemote"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
