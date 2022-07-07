repositories {
    maven { url = uri("https://nexus.velocitypowered.com/repository/maven-public/") }
}

dependencies {
    api(project(":api"))

    @Suppress("GradlePackageUpdate") // can't upgrade due to java version
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    // velocity-api
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
}

tasks {
    shadowJar {
        relocate("org.mariadb.jdbc", "net.azisaba.azipluginmessaging.libs.org.mariadb.jdbc")
        relocate("com.zaxxer.hikari", "net.azisaba.azipluginmessaging.libs.com.zaxxer.hikari")
        archiveFileName.set("AziPluginMessaging-Velocity-${project.version}.jar")
    }
}

