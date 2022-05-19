package net.azisaba.azipluginmessaging.api;

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
import java.util.concurrent.ConcurrentHashMap;

public class AziPluginMessagingConfig {
    public static final Map<String, String> saraShowServers = new ConcurrentHashMap<>();
    public static final Map<String, String> rankableServers = new ConcurrentHashMap<>();
    public static boolean debug = false;

    /**
     * Reloads all configuration from config file.
     */
    public static void reload() {
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
                                "saraShowServers: # this is meaningless in spigot",
                                "  life: life",
                                "  lifepve: life",
                                "rankableServers: # this is meaningless in spigot",
                                "  life: life",
                                "  lifepve: life",
                                "",
                                "# Whether to enable the debug logging.",
                                "# If turned on, many things will be logged, including public key of a connection and packet in/out, for example.",
                                "debug: false"
                        ),
                        StandardOpenOption.CREATE
                );
            } catch (IOException ex) {
                Logger.getCurrentLogger().warn("Failed to write config.yml", ex);
            }
        }
        try {
            YamlObject obj = new YamlConfiguration(configPath.toAbsolutePath().toString()).asObject();
            readMap(rankableServers, obj, "rankableServers");
            readMap(saraShowServers, obj, "saraShowServers");
            debug = obj.getBoolean("debug", false);
        } catch (IOException ex) {
            Logger.getCurrentLogger().warn("Failed to read config.yml", ex);
        }
    }

    private static void readMap(@NotNull Map<String, String> to, @NotNull YamlObject obj, @NotNull String configKey) {
        YamlObject mapObject = obj.getObject(configKey);
        if (mapObject != null) {
            mapObject.getRawData().forEach((key, value) -> to.put(key, String.valueOf(value)));
        }
    }
}
