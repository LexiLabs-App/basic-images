import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.multiplatform.library)
    alias(libs.plugins.kotlinx.binary.compatibility.validator)
    alias(libs.plugins.dokka)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
}

kotlin {

    // FORCES CHECK OF PUBLIC API DECLARATIONS
    // DON'T FORGET TO RUN `./gradlew apiDump`
    explicitApi()

    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-attach-js-exception")
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(), // mobile
        iosSimulatorArm64(), // mobile
        macosArm64(), // desktop
    ).forEach {
        it.binaries.framework {
            baseName = "basic-images"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.lexilabs.basic.logging)
            compileOnly(libs.compose.foundation)
            api(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.resources)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.ktor.client.ios)
        }
        macosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.ktor.client.ios)
        }
    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure{
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    android {
        namespace = "app.lexilabs.basic.images"
        compileSdk = rootProject.libs.versions.build.sdk.compile.get().toInt()
        minSdk = rootProject.libs.versions.build.sdk.min.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_26)
        }
    }
}

