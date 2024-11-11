repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven("https://repo.azisaba.net/repository/maven-public/")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

dependencies {
    api(project(":api"))

    @Suppress("GradlePackageUpdate") // can't upgrade due to java version
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    // velocity-api
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    // SpicyAzisaBan
    compileOnly("net.azisaba.spicyazisaban:common:2.0.0-dev-f2b83cc")
    compileOnly("xyz.acrylicstyle.util:promise:0.16.6")
    compileOnly("xyz.acrylicstyle:minecraft-util:1.0.0") {
        exclude("xyz.acrylicstyle.util", "http")
        exclude("xyz.acrylicstyle.util", "reflect")
    }
}

tasks {
    shadowJar {
        relocate("org.mariadb.jdbc", "net.azisaba.azipluginmessaging.libs.org.mariadb.jdbc")
        relocate("com.zaxxer.hikari", "net.azisaba.azipluginmessaging.libs.com.zaxxer.hikari")
        relocate("kotlin", "net.azisaba.spicyAzisaBan.libs.kotlin")
        relocate("util", "net.azisaba.spicyAzisaBan.libs.util")
        relocate("xyz.acrylicstyle.sql", "net.azisaba.spicyAzisaBan.libs.xyz.acrylicstyle.sql")
        relocate("xyz.acrylicstyle.mcutil", "net.azisaba.spicyAzisaBan.libs.xyz.acrylicstyle.mcutil")
        relocate("org.objectweb", "net.azisaba.spicyAzisaBan.libs.org.objectweb")
        relocate("org.json", "net.azisaba.spicyAzisaBan.libs.org.json")
        relocate("com.google.guava", "net.azisaba.spicyAzisaBan.libs.com.google.guava")
        archiveFileName.set("AziPluginMessaging-Velocity-${project.version}.jar")
    }
}

