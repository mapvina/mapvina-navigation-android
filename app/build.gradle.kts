plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
}

apply {
    from(file("${rootDir}/gradle/artifact-settings.gradle"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.mapvina.navigation.android.example"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.mapvina.navigation.android.example"
        compileSdk = 35
        minSdk = 23

        versionCode = 1
        versionName = project.properties.get("versionName") as String? ?: "0.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = false
        }

        release {
            isMinifyEnabled = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    dexOptions {
        maxProcessCount = 8
        javaMaxHeapSize = "2g"
        preDexLibraries = true
    }

    buildFeatures {
        viewBinding = true
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(project(":libandroid-navigation-ui"))

    implementation(libs.mapvina) {
        // Exclude old version of GeoJSON libs
        // At the moment a newer version - that supports Kotlin Multiplatform - is required to run navigation
        exclude(group = "io.github.mapvina", module = "android-sdk-geojson")
        exclude(group = "io.github.mapvina", module = "android-sdk-turf")
    }

    // Support libraries
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.cardview)

    implementation(libs.play.services.location)

    // Logging
    implementation(libs.timber)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Leak Canary
    implementation(libs.leakcanary)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.multidex)
}

apply {
    from(file("${rootDir}/gradle/developer-config.gradle"))
}
