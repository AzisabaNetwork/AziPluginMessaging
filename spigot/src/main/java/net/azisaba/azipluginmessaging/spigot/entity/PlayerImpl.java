package net.azisaba.azipluginmessaging.spigot.entity;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerImpl implements Player, PacketSender {
    private static final Map<UUID, Map.Entry<KeyPair, PublicKey>> KEY_MAP = new ConcurrentHashMap<>();
    private final org.bukkit.entity.Player handle;
    private KeyPair keyPair;
    private PublicKey remotePublicKey;
    private boolean encrypted = false;

    @Contract(value = "null -> fail", pure = true)
    public PlayerImpl(@Nullable org.bukkit.entity.Player handle) {
        if (handle == null) {
            throw new IllegalArgumentException("player is null");
        }
        this.handle = Objects.requireNonNull(handle);
        Map.Entry<KeyPair, PublicKey> key = KEY_MAP.get(handle.getUniqueId());
        if (key != null) {
            this.keyPair = key.getKey();
            if (key.getValue() != null) {
                this.remotePublicKey = key.getValue();
                this.encrypted = true;
            }
        }
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
    public void sendMessage(@NotNull String message) {
        getHandle().sendMessage(message);
    }

    @Override
    public boolean sendPacket(byte @NotNull [] data) {
        sendPacketFromPlayer(getHandle(), data);
        return true;
    }

    @NotNull
    @Override
    public KeyPair getKeyPair() {
        return Objects.requireNonNull(keyPair, "keyPair is null");
    }

    public void setKeyPair(@NotNull KeyPair keyPair) {
        KEY_MAP.put(getUniqueId(), new AbstractMap.SimpleImmutableEntry<>(keyPair, remotePublicKey));
        this.keyPair = keyPair;
    }

    @Override
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @Override
    public boolean isEncrypted() {
        return this.encrypted;
    }

    @NotNull
    @Override
    public PublicKey getRemotePublicKey() {
        return Objects.requireNonNull(remotePublicKey, "remotePublicKey is null");
    }

    @Override
    public void setRemotePublicKey(@NotNull PublicKey remotePublicKey) {
        Objects.requireNonNull(remotePublicKey, "remotePublicKey is null");
        KEY_MAP.put(getUniqueId(), new AbstractMap.SimpleImmutableEntry<>(keyPair, remotePublicKey));
        this.remotePublicKey = remotePublicKey;
    }

    public void setRemotePublicKeyInternal(@Nullable PublicKey remotePublicKey) {
        KEY_MAP.put(getUniqueId(), new AbstractMap.SimpleImmutableEntry<>(keyPair, remotePublicKey));
        this.remotePublicKey = remotePublicKey;
    }

    @Override
    public String toString() {
        return "PlayerImpl{" +
                "handle=" + handle +
                ", encrypted=" + encrypted +
                '}';
    }

    public static void sendPacketFromPlayer(@NotNull org.bukkit.entity.Player player, byte @NotNull [] data) {
        boolean success = false;
        try {
            player.sendPluginMessage(SpigotPlugin.plugin, Protocol.LEGACY_CHANNEL_ID, data);
            success = true;
        } catch (IllegalArgumentException ignore) {}
        try {
            player.sendPluginMessage(SpigotPlugin.plugin, Protocol.CHANNEL_ID, data);
        } catch (IllegalArgumentException e) {
            if (!success) throw new RuntimeException("Both Protocol.LEGACY_CHANNEL_ID and Protocol.CHANNEL_ID failed to send packet", e);
        }
    }
}
