repositories {
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public/") }
    maven { url = uri("https://repo.acrylicstyle.xyz/repository/maven-public/") }
}

dependencies {
    api(project(":api"))
    implementation("xyz.acrylicstyle.java-util:common:1.0.0-SNAPSHOT")
    compileOnlyApi("xyz.acrylicstyle.java-util:common:1.0.0-SNAPSHOT")

    // spigot-api
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveFileName.set("AziPluginMessaging-Spigot-${project.version}.jar")
    }
}
