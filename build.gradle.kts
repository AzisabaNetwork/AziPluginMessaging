plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "net.azisaba.azipluginmessaging"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

subprojects {
    group = parent!!.group
    version = parent!!.version

    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        mavenCentral()
    }
}

allprojects {
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
        withSourcesJar()
        withJavadocJar()
    }

    val javaComponent = components["java"] as AdhocComponentWithVariants
    javaComponent.withVariantsFromConfiguration(configurations["sourcesElements"]) {
        skip()
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifact(tasks.getByName("sourcesJar"))
            }
        }
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }
    }
}
