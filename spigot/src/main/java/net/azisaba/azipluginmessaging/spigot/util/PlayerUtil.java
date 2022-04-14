package net.azisaba.azipluginmessaging.spigot.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class PlayerUtil {
    private static final Gson GSON = new Gson();

    @Contract("_ -> new")
    public static @NotNull Player resolveNameOrUUID(@NotNull String nameOrUUID) {
        try {
            UUID uuid = UUID.fromString(nameOrUUID);
            return new SimplePlayer(uuid, null);
        } catch (IllegalArgumentException ignored) {}
        return PlayerImpl.of(Objects.requireNonNull(Bukkit.getPlayerExact(nameOrUUID), "player " + nameOrUUID + " does not exist"));
    }

    public static @NotNull Player getOfflinePlayer(@NotNull String nameOrUUID) {
        try {
            Player player = resolveNameOrUUID(nameOrUUID);
            if (player.getUsername() == null) return player;
        } catch (NullPointerException ignored) {}
        try {
            return new SimplePlayer(fetchUUIDFromMojangAPI(nameOrUUID), nameOrUUID);
        } catch (IOException e) {
            String message = "Failed to fetch UUID of " + nameOrUUID + " from Mojang API.\n" +
                    "Possible reasons:\n" +
                    "  - the player does not exist\n" +
                    "  - the Mojang API is down\n" +
                    "  - your internet connection is down";
            throw new RuntimeException(message, e);
        }
    }

    public static @NotNull UUID fetchUUIDFromMojangAPI(@NotNull String name) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("Accept", "application/json");
        conn.addRequestProperty("User-Agent", "AziPluginMessaging/" + SpigotPlugin.plugin.getDescription().getVersion());
        conn.setRequestMethod("GET");
        conn.connect();
        StringBuilder sb = new StringBuilder();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP response code: " + responseCode);
        }
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        conn.disconnect();
        return parseUUID(GSON.fromJson(sb.toString(), JsonObject.class).get("id").getAsString());
    }

    @Contract("_ -> new")
    public static @NotNull UUID parseUUID(@NotNull String uuid) {
        return UUID.fromString(uuid.replaceAll("(?i)([\\da-f]{8})([\\da-f]{4})([\\da-f]{4})([\\da-f]{4})([\\da-f]{12})", "$1-$2-$3-$4-$5"));
    }
}
