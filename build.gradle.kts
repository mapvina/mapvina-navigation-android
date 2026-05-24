plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.kotlin.dokka) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.nexus)
}

apply {
    from(file("${rootDir}/gradle/artifact-settings.gradle"))
    from(file("${rootDir}/gradle/publish-root.gradle"))
}

nexusPublishing {
    repositories {
        sonatype {
            useStaging.set((project.properties.get("isSnapshot") as Boolean).not())
            stagingProfileId.set(project.extra["sonatypeStagingProfileId"] as String?)

            username.set(project.extra["mavenCentralUsername"] as String?)
            password.set(project.extra["mavenCentralPassword"] as String?)

            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }

    transitionCheckOptions {
        maxRetries.set(120)
        delayBetween.set(java.time.Duration.ofSeconds(10))
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

allprojects {
    plugins.withId("signing") {
        val privateKeyFile = file("/Volumes/DATA/MapVina/private-key.asc")
        if (privateKeyFile.exists()) {
            val privateKey = privateKeyFile.readText()
            val password = project.findProperty("signing.password") as String? ?: System.getenv("SIGNING_PASSWORD")
            val keyId = project.findProperty("signing.keyId") as String? ?: "8B10EF76"
            extensions.configure<SigningExtension> {
                useInMemoryPgpKeys(keyId, privateKey, password)
            }
        }
    }
}
