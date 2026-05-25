plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
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
    namespace = "io.github.mapvina.navigation.android.navigation.ui.v5"

    defaultConfig {
        compileSdk = 35
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        buildConfigField(
            "String",
            "MAPVINA_NAVIGATION_SDK_IDENTIFIER",
            String.format("\"%s\"", "mapvina-navigation-ui-android")
        )
        buildConfigField(
            "String",
            "MAPVINA_NAVIGATION_EVENTS_USER_AGENT", String.format(
                "\"mapvina-navigation-ui-android/%s\"",
                project.extra["versionName"]
            )
        )

        consumerProguardFiles("proguard-consumer.pro")
    }

    lint {
        baseline = file("lint-baseline.xml")
    }

    configurations.create("javadocDeps")

    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = true
        }
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // Navigation SDK
    api(project(":mapvina-navigation-core"))

    api(libs.mapvina.annotation)
    implementation(libs.mapvina)
    implementation("io.github.mapvina:android-sdk-geojson:1.0.0")
    implementation("io.github.mapvina:android-sdk-turf:1.0.0")

    // Mapbox SDKs (needed for requests)
    api(libs.mapbox.geojson)

    // Support libraries
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)

    // AutoValues
    annotationProcessor(libs.autovalue)
    implementation(libs.autovalue.annotations)

    // Picasso
    implementation(libs.picasso)

    // Timber
    implementation(libs.timber)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
}

// Exclude old version of GeoJSON libs if any conflicts remain, but keep JVM geojson and turf for navigation compatibility
configurations {
    configureEach {
        // Excludes removed since namespaces were safely moved under distinct package names io.github.mapvina.geojson and io.github.mapvina.spatialk.geojson
    }
}

apply {
    from(file("javadoc.gradle"))
    from(file("${rootDir}/gradle/publish-android.gradle"))
}
