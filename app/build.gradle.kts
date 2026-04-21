plugins {
    // 套用 Android Application plugin
    alias(libs.plugins.android.application)
    // 套用 Kotlin Android plugin
    alias(libs.plugins.kotlin.android)
    // 啟用 Jetpack Compose
    alias(libs.plugins.kotlin.compose)
    // 套用 Hilt Gradle plugin
    alias(libs.plugins.hilt.android)
    // 啟用 kapt，讓 Hilt compiler 可以產生程式碼
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.example.bluetoothscanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bluetoothscanner"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Android KTX 基本擴充
    implementation(libs.androidx.core.ktx)
    // Lifecycle runtime KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Compose Activity 支援
    implementation(libs.androidx.activity.compose)

    // 使用 Compose BOM 統一 Compose 版本
    implementation(platform(libs.androidx.compose.bom))
    // Compose UI
    implementation(libs.androidx.compose.ui)
    // Compose graphics
    implementation(libs.androidx.compose.ui.graphics)
    // Compose Preview
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Material 3
    implementation(libs.androidx.compose.material3)
    // 單元測試
    testImplementation(libs.junit)
    // Android 測試
    androidTestImplementation(libs.androidx.junit)
    // Espresso
    androidTestImplementation(libs.androidx.espresso.core)
    // Compose 測試也使用 BOM
    androidTestImplementation(platform(libs.androidx.compose.bom))
    // Compose UI test
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // Debug tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Debug test manifest
    debugImplementation(libs.androidx.compose.ui.test.manifest)



    // Compose 觀察 lifecycle 狀態時常用
    implementation(libs.androidx.lifecycle.runtime.compose)
    // Compose 中取得 ViewModel 會用到
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    // Hilt 與 Navigation Compose 整合
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt runtime
    implementation(libs.hilt.android)
    // Hilt compiler
    kapt(libs.hilt.android.compiler)

}