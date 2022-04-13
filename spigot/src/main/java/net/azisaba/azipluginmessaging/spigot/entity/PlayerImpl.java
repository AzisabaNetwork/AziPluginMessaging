package net.azisaba.azipluginmessaging.spigot.entity;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class PlayerImpl implements Player, PacketSender {
    private final org.bukkit.entity.Player handle;

    @Contract(value = "null -> fail", pure = true)
    public PlayerImpl(@Nullable org.bukkit.entity.Player handle) {
        if (handle == null) {
            throw new IllegalArgumentException("player is null");
        }
        this.handle = Objects.requireNonNull(handle);
    }

    @NotNull
    public org.bukkit.entity.Player getHandle() {
        return handle;
    }

    @Override
    public @NotNull String getUsername() {
        return getHandle().getName();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return getHandle().getUniqueId();
    }

    @Override
    public boolean sendPacket(byte @NotNull [] data) {
        sendPacketFromPlayer(getHandle(), data);
        return true;
    }

    public static void sendPacketFromPlayer(@NotNull org.bukkit.entity.Player player, byte @NotNull [] data) {
        try {
            player.sendPluginMessage(SpigotPlugin.plugin, Protocol.LEGACY_CHANNEL_ID, data);
        } catch (IllegalArgumentException ignore) {}
        try {
            player.sendPluginMessage(SpigotPlugin.plugin, Protocol.CHANNEL_ID, data);
        } catch (IllegalArgumentException ignore) {}
    }
}
