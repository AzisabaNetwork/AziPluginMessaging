rootProject.name = "AziPluginMessaging"
include("api")
include("velocity")
include("spigot")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}
