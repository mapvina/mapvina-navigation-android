import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.dokka)
}

apply {
    from(file("${rootDir}/gradle/artifact-settings.gradle"))
    from(file("${rootDir}/gradle/publish-kmp.gradle"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)

    androidTarget {
        publishLibraryVariants("release")
    }

    // val xcf = XCFrameworkConfig(project)
    // listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    //     iosTarget.binaries.framework {
    //         baseName = "mapvina-navigation-core"
    //         xcf.add(this)
    //     }
    // }

    sourceSets {
        commonMain.dependencies {
            api(libs.mapvina.geojson)
            api(libs.mapvina.geojson.turf)
            api(libs.mapvina.geojson.polyline)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kermit)
        }

        // `commonTest` can not be used here, because `mockk` is only valid for JVM targets
        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockk)

            // For Android specific tests
            implementation(libs.robolectric)
        }

        androidMain.dependencies {
            implementation(libs.mapvina)

            // Location by Play Services
            // Will only used, when added by the client app
            compileOnly(libs.play.services.location)
        }
    }

    // cocoapods {
    //     version = project.properties.get("versionName") as String? ?: "0.0.0"
    //     summary = "MapVina navigation core library"
    //     homepage = "https://github.io/github/mapvina/mapvina-navigation-android/"
    // 
    //     name = "MapVinaNavigationCore"
    // 
    //     framework {
    //         baseName = "MapVinaNavigationCore"
    //         isStatic = false
    // 
    //         transitiveExport = false
    //         export(libs.mapvina.geojson)
    //         export(libs.mapvina.geojson.turf)
    //     }
    // }
}

android {
    namespace = "io.github.mapvina.navigation.core"

    defaultConfig {
        compileSdk = 35
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "MAPVINA_NAVIGATION_SDK_IDENTIFIER",
            String.format("\"%s\"", "mapvina-navigation-android")
        )
        buildConfigField(
            "String",
            "MAPVINA_NAVIGATION_VERSION_NAME", String.format("\"%s\"", project.properties.get("versionName"))
        )
        buildConfigField(
            "String",
            "MAPVINA_NAVIGATION_EVENTS_USER_AGENT", String.format(
                "\"mapvina-navigation-android/%s\"",
                project.properties.get("versionName")
            )
        )

        consumerProguardFiles("proguard-consumer.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

// Exclude old version of GeoJSON libs
// At the moment a newer version - that supports Kotlin Multiplatform - is required to run navigation
configurations {
    configureEach {
        exclude(group = "io.github.mapvina", module = "android-sdk-geojson")
        exclude(group = "io.github.mapvina", module = "android-sdk-turf")
    }
}
