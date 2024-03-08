plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        withJava()

    }

    sourceSets {
        commonMain {
            dependencies {

                implementation(project(":server"))


                implementation(project(":common:core"))

                implementation(project(":common:umbrella-core"))
                implementation(project(":common:umbrella-compose"))
                implementation(project(":common:utils-compose"))
                implementation(project(":common:utils"))
                //implementation(project(":common:auth:compose"))
                implementation(project(":common:settings:compose"))
//                implementation(project(":common:launch:compose"))
                implementation(project(":common:auth:compose"))


                implementation(Deps.Decompose.compose)
                implementation(Deps.Decompose.decompose)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                implementation(Deps.MVIKotlin.mvikotlinMain)
                implementation(Deps.MVIKotlin.mvikotlin)
                implementation(Deps.MVIKotlin.mvikotlinExtensionsCoroutines)

                implementation(compose.desktop.common)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)


            }
        }

        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                }
                implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window:0.12.0")
//                //Server
//                implementation(project(":common:auth:ktor"))
//
//                implementation("io.ktor:ktor-server-content-negotiation-jvm")
//                implementation("io.ktor:ktor-server-core-jvm")
//                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
//                implementation("io.ktor:ktor-server-netty-jvm")
//                implementation("ch.qos.logback:logback-classic:1.4.11")
//
//                implementation("org.postgresql:postgresql:42.5.1")
//
//                implementation("org.jetbrains.exposed:exposed-core:0.44.1")
//                implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
//                implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
//
//                implementation(Deps.Kotlin.DateTime.dateTime)
            }
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
    gradlePluginPortal()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}

compose.desktop {
    application {
        mainClass = "Main_desktopKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )

            packageName = "PansionApp"
            packageVersion = "1.0.0"
            windows {
                menuGroup = "PansionApp"
                upgradeUuid = "134213"
            }
        }
    }
}