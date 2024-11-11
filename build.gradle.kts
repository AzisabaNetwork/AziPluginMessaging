plugins {
    java
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.3" apply false
}

group = "net.azisaba.azipluginmessaging"
version = "4.1.0"

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
        plugin("com.gradleup.shadow")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
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
        repositories {
            maven {
                name = "repo"
                credentials(PasswordCredentials::class)
                url = uri(
                    if (project.version.toString().endsWith("SNAPSHOT"))
                        project.findProperty("deploySnapshotURL") ?: System.getProperty("deploySnapshotURL", "https://repo.azisaba.net/repository/maven-snapshots/")
                    else
                        project.findProperty("deployReleasesURL") ?: System.getProperty("deployReleasesURL", "https://repo.azisaba.net/repository/maven-releases/")
                )
            }
        }

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
