plugins {
//    id("android-setup")
    id("compose-setup")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
//                implementation("androidx.core:core:1.12.0")
                api("androidx.core:core-ktx:1.12.0")
                implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
            }
        }
        commonMain {
            dependencies {
                implementation(project(":common:utils"))
                implementation(project(":common:core"))
//                implementation(libs.moko.resources.compose)
                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)
            }
        }
    }
}