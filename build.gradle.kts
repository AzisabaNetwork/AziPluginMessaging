plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "net.azisaba"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
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
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        mavenCentral()
    }
}
