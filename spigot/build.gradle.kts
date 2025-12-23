repositories {
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public/") }
    maven { url = uri("https://repo.azisaba.net/repository/maven-public/") }
}

dependencies {
    api(project(":api"))
    implementation("xyz.acrylicstyle.java-util:common:2.1.0")
    compileOnlyApi("xyz.acrylicstyle.java-util:common:2.1.0")

    // spigot-api
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            include("**")
            val tokenReplacementMap = mapOf(
                "VERSION" to project.version
            )
            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokenReplacementMap)
        }
        filteringCharset = "UTF-8"
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    shadowJar {
        archiveFileName.set("AziPluginMessaging-Spigot-${project.version}.jar")
    }
}
