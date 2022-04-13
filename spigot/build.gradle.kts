repositories {
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/groups/public/")
    }
}

dependencies {
    api(project(":api"))

    // spigot-api
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveFileName.set("AziPluginMessaging-Spigot-${project.version}.jar")
    }
}
