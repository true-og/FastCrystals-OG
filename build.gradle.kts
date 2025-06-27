plugins {
    java
    eclipse
    id("com.diffplug.spotless") version "7.0.4"
    id("com.gradleup.shadow") version "8.3.6"
}

group = "xyz.exyron.fastcrystals"
version = "1.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.gradleup.shadow")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
            vendor = JvmVendorSpec.GRAAL_VM
        }
    }

    spotless {
        java {
            removeUnusedImports()
            palantirJavaFormat()
        }
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.shadowJar {
        archiveClassifier.set("")
        minimize()
    }

    tasks.build {
        dependsOn(tasks.spotlessApply)
        dependsOn(tasks.shadowJar)
    }

    tasks.jar { archiveClassifier.set("part") }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-parameters", "-Xlint:deprecation"))
        options.encoding = "UTF-8"
        options.isFork = true
    }
}

