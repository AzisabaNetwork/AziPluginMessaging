repositories {
    maven { url = uri("https://nexus.velocitypowered.com/repository/maven-public/") }
}

dependencies {
    api(project(":api"))

    // velocity-api
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
}

tasks {
    shadowJar {
        archiveFileName.set("AziPluginMessaging-Velocity-${project.version}.jar")
    }
}

