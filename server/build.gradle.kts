import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinJvm)
    application
    kotlin("plugin.serialization") version "2.0.20"
    id("com.github.gmazzo.buildconfig") version "5.5.0"
}

val dbUser: String = gradleLocalProperties(rootDir).getProperty("sv.dbUser")
val dbPassword: String = gradleLocalProperties(rootDir).getProperty("sv.dbPassword")
val authToken: String = gradleLocalProperties(rootDir).getProperty("auth.token")

buildConfig {
    buildConfigField("DB_USER", dbUser)
    buildConfigField("DB_PASSWORD", dbPassword)
    buildConfigField("AUTH_TOKEN", authToken)
}

group = "io.github.feliperce.aabtoapk"
version = "1.0.0"
application {
    mainClass.set("io.github.feliperce.aabtoapk.ApplicationKt")
    //applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.extractor)
    implementation(projects.sharedRemote)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.ktor.serialization.json)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.json)
    implementation(libs.postgresql)

    implementation(libs.koin.ktor)

    implementation(libs.quartz)
}