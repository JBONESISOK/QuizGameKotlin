plugins {
    kotlin("multiplatform") version "1.6.10"
    application
    kotlin("plugin.serialization") version "1.6.20"
}

group = "me.origi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:1.6.7")
                implementation("io.ktor:ktor-html-builder:1.6.7")
                implementation("io.ktor:ktor-client-core:1.6.7")
                implementation("io.ktor:ktor-client-cio:1.6.7")
                implementation("io.ktor:ktor-client-serialization:1.6.7")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:1.6.7")
                implementation("io.ktor:ktor-client-json:1.6.7")
                implementation("io.ktor:ktor-client-serialization:1.6.7")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.299-kotlin-1.6.10")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.299-kotlin-1.6.10")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-css:17.0.2-pre.299-kotlin-1.6.10")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("me.origi.application.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}