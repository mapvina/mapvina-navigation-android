plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.mapvina.navigation.sample.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.mapvina.navigation.sample.android"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.mapvina) {
        // Exclude old version of GeoJSON libs
        // At the moment a newer version - that supports Kotlin Multiplatform - is required to run navigation
        exclude(group = "io.github.mapvina", module = "android-sdk-geojson")
        exclude(group = "io.github.mapvina", module = "android-sdk-turf")
    }

    implementation(libs.mapvina.navigation.core)
    implementation(libs.mapvina.geojson)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}