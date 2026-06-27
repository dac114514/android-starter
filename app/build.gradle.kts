import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
}

compose.resources {
    packageOfResClass = "dev.dac114514.starter.generated.resources"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":ui"))
            implementation(libs.composables.icons.lucide)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.composables.unstyled)
            implementation(libs.composables.unstyled.build.modifier)
            implementation(libs.compose.components.resources)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "dev.dac114514.starter"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "dev.dac114514.starter"
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.compile.sdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
}
