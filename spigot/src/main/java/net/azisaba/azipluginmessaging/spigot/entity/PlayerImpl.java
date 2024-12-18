package net.azisaba.azipluginmessaging.spigot.entity;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerImpl implements Player, PacketSender {
    private static final Map<UUID, PlayerImpl> MAP = new ConcurrentHashMap<>();
    private org.bukkit.entity.Player handle;
    private KeyPair keyPair;
    private PublicKey remotePublicKey;
    private boolean encrypted = false;
    public String challenge = null;
    public AtomicInteger joins = new AtomicInteger();
    private final AtomicBoolean encryptedOnce = new AtomicBoolean(false);

    @Contract(value = "null -> fail", pure = true)
    private PlayerImpl(@Nullable org.bukkit.entity.Player handle) {
        if (handle == null) {
            throw new IllegalArgumentException("player is null");
        }
        this.handle = Objects.requireNonNull(handle);
    }

    @NotNull
    public static PlayerImpl of(@NotNull org.bukkit.entity.Player player) {
        PlayerImpl p = MAP.computeIfAbsent(player.getUniqueId(), u -> new PlayerImpl(player));
        p.handle = player;
        return p;
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
        this.keyPair = keyPair;
    }

    @Override
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;

        // clear challenge, so any new encryption requests will be denied.
        this.challenge = null;
    }

    @Override
    public boolean isEncrypted() {
        return this.encrypted;
    }

    @Override
    public void setEncryptedOnce() {
        encryptedOnce.set(true);
    }

    @Override
    public boolean consumeEncryptedOnce() {
        return encryptedOnce.compareAndSet(true, false);
    }

    @NotNull
    @Override
    public PublicKey getRemotePublicKey() {
        return Objects.requireNonNull(remotePublicKey, "remotePublicKey is null");
    }

    @Override
    public void setRemotePublicKey(@NotNull PublicKey remotePublicKey) {
        Objects.requireNonNull(remotePublicKey, "remotePublicKey is null");
        this.remotePublicKey = remotePublicKey;
    }

    public void setRemotePublicKeyInternal(@Nullable PublicKey remotePublicKey) {
        this.remotePublicKey = remotePublicKey;
    }

    @Override
    public boolean isChallengeEquals(@NotNull String challenge) {
        if (this.challenge == null) return false;
        return this.challenge.equals(challenge);
    }

    @Override
    public String toString() {
        return "PlayerImpl{" +
                "handle=" + handle +
                ", publicKey=" + (keyPair == null ? null : EncryptionUtil.encodePublicKey(keyPair.getPublic())) +
                ", remotePublicKey=" + (remotePublicKey == null ? null : EncryptionUtil.encodePublicKey(remotePublicKey)) +
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
            if (!success) throw new RuntimeException("Failed to send packet", e);
        }
    }
}
