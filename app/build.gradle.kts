import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.esupplemental"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    val localProperties = Properties().apply {
        val propertiesFile = rootProject.file("local.properties")
        if (propertiesFile.exists()) load(propertiesFile.inputStream())
    }
    val apiKey = localProperties.getProperty("appApiKey") ?: ""

    defaultConfig {
        applicationId = "com.esupplemental"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "APP_API_KEY", "\"$apiKey\"")
        buildConfigField("boolean", "ALLOW_GAME_PROGRESSION_BYPASS", "false")
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("boolean", "ALLOW_GAME_PROGRESSION_BYPASS", "false")
        }

        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("debug")
            buildConfigField("boolean", "ALLOW_GAME_PROGRESSION_BYPASS", "true")
        }

        create("demo") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".demo"
            versionNameSuffix = "-demo"
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("debug")
            buildConfigField("boolean", "ALLOW_GAME_PROGRESSION_BYPASS", "true")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("boolean", "ALLOW_GAME_PROGRESSION_BYPASS", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // ── Core ──────────────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ── Compose BOM ───────────────────────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material.icons.extended.v173)

    // ── Navigation Compose ───────────────────────────────────────────────────
    // ─
    implementation(libs.androidx.navigation.compose)

    // ── ViewModel Compose ─────────────────────────────────────────────────────
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ── Paging ──────────────────────────────────────────────────────────────
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // ── Coroutines ────────────────────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)

    // ── Koin ──────────────────────────────────────────────────────────────
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.workmanager)

    // ── WorkManager ───────────────────────────────────────────────────────
    implementation(libs.androidx.work.runtime)

    // ── Room ──────────────────────────────────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.gson)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.ui)
    implementation(libs.coil.compose)

    // ── Debug / Test ──────────────────────────────────────────────────────────
    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    add("stagingImplementation", libs.androidx.ui.tooling)
    add("stagingImplementation", libs.androidx.ui.test.manifest)
    add("demoImplementation", libs.androidx.ui.tooling)
    add("demoImplementation", libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
