@file:Suppress("OPT_IN_USAGE")

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}


android {
    namespace = "com.nevrozq.pansion.android"
    compileSdk = 34
}

kotlin {
    //Targets
    jvm()
    androidTarget()

    applyDefaultHierarchyTemplate()

    iosX64()
    iosArm64()
    iosSimulatorArm64()


    js(IR) {
        browser()
//        useCommonJs()
        binaries.executable()
    }
    wasmJs {
        browser()
//        useCommonJs()
        binaries.executable()
    }

//        js(IR) {
//        browser()
//        binaries.executable()
//    }
//RIP

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
        }
    }

    //JVM
    jvmToolchain(17)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

