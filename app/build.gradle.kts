import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.movieslistapp"
    compileSdk = 36

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    val omdbApiKey = localProperties.getProperty("OMDB_API_KEY") ?: ""
    val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""
    val youtubeDataV3Key = localProperties.getProperty("YOUTUBE_DATA_V3") ?: ""
    defaultConfig {
        applicationId = "com.example.movieslistapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "YOUTUBE_DATAV3_API_KEY", "\"$youtubeDataV3Key\"")
        buildConfigField("String", "API_KEY", "\"$omdbApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adapters)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // 🔹 Compose BOM (handles ALL compose versions)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    //material extended
    implementation("androidx.compose.material:material-icons-extended")

// 🔹 Core Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

// 🔹 Activity + Compose
    implementation("androidx.activity:activity-compose:1.9.2")

// 🔹 ViewModel + Compose integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

// 🔹 Navigation + Compose
    implementation(libs.androidx.navigation.compose)

// 🔹 Kotlin Coroutines (for pagination)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// 🔹 Flow helpers (already included, but explicit is fine)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

// 🔹 Koin (DI) — Compose friendly
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")

    // 🔹 Retrofit (Networking)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // 🔹 OkHttp Logging Interceptor (for debugging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 🔹 Coil (Image Loading)
    implementation(libs.coil.compose)

    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //shimmer dependency
    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.0")

    // Add TrailerAI SDK
    implementation("com.example.aitrailersdk:trailerai-core:2.1.0")

    implementation(project(":trailer-player"))

}
