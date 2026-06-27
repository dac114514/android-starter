import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kotlin.multiplatform.library)
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

    android {
        namespace = "com.composables.ui"
        compileSdk = libs.versions.android.compile.sdk.get().toInt()
        minSdk = libs.versions.android.min.sdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.foundation)
            implementation(libs.composables.unstyled)
            api(libs.composables.unstyled.stack)
            api(libs.composables.unstyled.theming)
            implementation(libs.composables.unstyled.build.modifier)
            implementation(libs.composables.interaction.capabilities)
            implementation(libs.composables.ripple.indication)
        }
    }
}

group = "dev.dac114514.starter"
version = "0.1.0"
