package net.azisaba.azipluginmessaging.api;

import net.azisaba.azipluginmessaging.api.yaml.YamlArray;
import net.azisaba.azipluginmessaging.api.yaml.YamlConfiguration;
import net.azisaba.azipluginmessaging.api.yaml.YamlObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AziPluginMessagingConfig {
    // Common
    public static boolean debug = false;

    // Spigot

    // Velocity
    public static final Map<String, String> servers = new ConcurrentHashMap<>();
    public static final Map<String, String> saraShowServers = new ConcurrentHashMap<>();
    public static final Map<String, String> rankableServers = new ConcurrentHashMap<>();
    public static final Set<String> unpunishableServers = ConcurrentHashMap.newKeySet();
    @SuppressWarnings("DeprecatedIsStillUsed") // not used outside this class
    @Deprecated
    public static final Map<String, String> contextualServers = new ConcurrentHashMap<>();

    /**
     * Reloads all configuration from config file.
     */
    public static void reload() {
        debug = false;
        servers.clear();
        saraShowServers.clear();
        rankableServers.clear();
        Path dataDirectory = new File("plugins/AziPluginMessaging").toPath();
        Path configPath = dataDirectory.resolve("config.yml");
        if (!Files.exists(configPath)) {
            try {
                if (!Files.exists(dataDirectory)) {
                    Files.createDirectory(dataDirectory);
                }
                Files.write(
                        configPath,
                        Arrays.asList(
                                "#",
                                "# AziPluginMessaging configuration",
                                "#",
                                "",
                                "##############################",
                                "#    Common configuration    #",
                                "##############################",
                                "#",
                                "# These configurations are used by both Spigot and Velocity sides.",
                                "#",
                                "",
                                "# Whether to enable the debug logging.",
                                "# If turned on, many things will be logged, including public key of a connection and packet in/out, for example.",
                                "debug: false",
                                "",
                                "##############################",
                                "#    Spigot configuration    #",
                                "##############################",
                                "#",
                                "# These configurations are only used by Spigot side and ignored entirely by Velocity side.",
                                "#",
                                "",
                                "# well, there is nothing, for now.",
                                "",
                                "##############################",
                                "#   Velocity configuration   #",
                                "##############################",
                                "#",
                                "# These configurations are only used by Velocity side and ignored entirely by Spigot side.",
                                "#",
                                "",
                                "# Map of servers (server name in velocity: server name in LuckPerms)",
                                "# This is used for situations where saraShowServers/rankableServers does not apply.",
                                "# This map is used in:",
                                "# - ProxyboundSetPrefixPacket",
                                "# - ProxyboundClearPrefixPacket",
                                "servers: # this is meaningless in spigot",
                                "  life: life",
                                "  lifepve: life",
                                "",
                                "# Map of servers that ProxyboundToggleSaraShowPacket is allowed on.",
                                "# When the proxy receives a packet from non-enabled server, the proxy will drop the packet.",
                                "saraShowServers: # this is meaningless in spigot",
                                "  life: life",
                                "  lifepve: life",
                                "",
                                "# Map of servers that ProxyboundSetRankPacket is allowed on.",
                                "# When the proxy receives a packet from non-enabled server, the proxy will drop the packet.",
                                "rankableServers: # this is meaningless in spigot",
                                "  life: life",
                                "  lifepve: life",
                                "",
                                "# List of servers that ProxyboundPunishPacket is not allowed on.",
                                "unpunishableServers: # this is meaningless in spigot",
                                "  - life9000",
                                "",
                                "# Database settings (required)",
                                "# This setting is used for setting the rank temporary.",
                                "database:",
                                "  # (scheme)://(host):(port)/(database)",
                                "  # Keep the line commented out unless you get an obvious error message indicating that the driver was not found.",
                                "  # Default driver (net.azisaba.taxoffice.libs.org.mariadb.jdbc.Driver) points to the bundled MariaDB driver in the TaxOffice jar.",
                                "  #driver: net.azisaba.azipluginmessaging.libs.org.mariadb.jdbc.Driver",
                                "",
                                "  # change to jdbc:mysql if you want to use MySQL instead of MariaDB",
                                "  scheme: jdbc:mariadb",
                                "  hostname: localhost",
                                "  port: 3306",
                                "  name: azipm",
                                "  username: azipm",
                                "  password: azipm",
                                "  properties:",
                                "    useSSL: true",
                                "    verifyServerCertificate: true",
                                "    prepStmtCacheSize: 250",
                                "    prepStmtCacheSqlLimit: 2048",
                                "    cachePrepStmts: true",
                                "    useServerPrepStmts: true # use server-side prepared statements for performance boost",
                                "    socketTimeout: 30000 # milliseconds",
                                "    useLocalSessionState: true",
                                "    rewriteBatchedStatements: true",
                                "    maintainTimeStats: false"
                        ),
                        StandardOpenOption.CREATE
                );
            } catch (IOException ex) {
                Logger.getCurrentLogger().warn("Failed to write config.yml", ex);
            }
        }
        try {
            YamlObject obj = new YamlConfiguration(configPath.toAbsolutePath().toString()).asObject();
            debug = obj.getBoolean("debug", false);
            if (AziPluginMessagingProvider.get().getEnvironmentType() == EnvironmentType.VELOCITY) {
                readMap(contextualServers, obj, "contextualServers", true);
                servers.putAll(contextualServers);
                readMap(servers, obj, "servers");
                readMap(rankableServers, obj, "rankableServers");
                readMap(saraShowServers, obj, "saraShowServers");
                readSet(unpunishableServers, obj, "unpunishableServers");
                AziPluginMessagingProvider.get().getProxy().loadConfig(obj);
            }
        } catch (IOException ex) {
            Logger.getCurrentLogger().warn("Failed to read config.yml", ex);
        }
    }

    private static void readMap(@NotNull Map<String, String> to, @NotNull YamlObject obj, @NotNull String configKey) {
        readMap(to, obj, configKey, false);
    }

    private static void readMap(@NotNull Map<String, String> to, @NotNull YamlObject obj, @NotNull String configKey, boolean deprecated) {
        YamlObject mapObject = obj.getObject(configKey);
        if (mapObject != null) {
            if (deprecated) {
                Logger.getCurrentLogger().warn("The configuration key '" + configKey + "' is deprecated and will be removed in the future.");
            }
            mapObject.getRawData().forEach((key, value) -> to.put(key, String.valueOf(value)));
        }
    }

    private static void readSet(@NotNull Set<String> to, @NotNull YamlObject obj, @NotNull String configKey) {
        YamlArray array = obj.getArray(configKey);
        if (array != null) {
            to.addAll(array.mapToString());
        }
    }
}
