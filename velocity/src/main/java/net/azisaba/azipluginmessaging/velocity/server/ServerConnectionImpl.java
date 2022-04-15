package net.azisaba.azipluginmessaging.velocity.server;

import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.server.ServerInfo;
import net.azisaba.azipluginmessaging.velocity.entity.PlayerImpl;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConnectionImpl implements ServerConnection {
    private static long lastCleaned = System.currentTimeMillis();
    private static final Map<com.velocitypowered.api.proxy.ServerConnection, Map.Entry<KeyPair, PublicKey>> KEY_MAP = new ConcurrentHashMap<>();
    private final com.velocitypowered.api.proxy.ServerConnection handle;
    private KeyPair keyPair;
    private PublicKey publicKey;
    private boolean encrypted = false;

    public ServerConnectionImpl(@NotNull com.velocitypowered.api.proxy.ServerConnection handle) {
        this.handle = handle;
        Map.Entry<KeyPair, PublicKey> entry = KEY_MAP.get(handle);
        if (entry != null) {
            keyPair = entry.getKey();
            publicKey = entry.getValue();
            encrypted = true;
        }
        // check last cleaned time
        if (System.currentTimeMillis() - lastCleaned > 30000) {
            clean();
        }
    }

    private static void clean() {
        lastCleaned = System.currentTimeMillis();
        List<com.velocitypowered.api.proxy.ServerConnection> toRemove = new ArrayList<>();
        for (Map.Entry<com.velocitypowered.api.proxy.ServerConnection, Map.Entry<KeyPair, PublicKey>> e : KEY_MAP.entrySet()) {
            if (!e.getKey().getPlayer().isActive()) {
                toRemove.add(e.getKey());
            }
        }
        for (com.velocitypowered.api.proxy.ServerConnection connection : toRemove) {
            KEY_MAP.remove(connection);
        }
    }

    @NotNull
    public com.velocitypowered.api.proxy.ServerConnection getHandle() {
        return handle;
    }

    @Override
    public @NotNull Player getPlayer() {
        return new PlayerImpl(getHandle().getPlayer());
    }

    @Override
    public @NotNull ServerInfo getServerInfo() {
        return new ServerInfoImpl(getHandle().getServerInfo());
    }

    @Override
    public boolean sendPacket(byte @NotNull [] data) {
        return getHandle().sendPluginMessage(new LegacyChannelIdentifier(Protocol.LEGACY_CHANNEL_ID), data) | // yes , one | to send both packets
                getHandle().sendPluginMessage(MinecraftChannelIdentifier.from(Protocol.CHANNEL_ID), data);
    }

    @Override
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @Override
    public boolean isEncrypted() {
        return encrypted;
    }

    @Override
    public @NotNull KeyPair getKeyPair() {
        return Objects.requireNonNull(keyPair);
    }

    @Override
    public void setKeyPair(@NotNull KeyPair keyPair) {
        KEY_MAP.put(handle, new AbstractMap.SimpleImmutableEntry<>(keyPair, publicKey));
        this.keyPair = keyPair;
    }

    @Override
    public @NotNull PublicKey getRemotePublicKey() {
        return publicKey;
    }

    @Override
    public void setRemotePublicKey(@NotNull PublicKey publicKey) {
        KEY_MAP.put(handle, new AbstractMap.SimpleImmutableEntry<>(keyPair, publicKey));
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "ServerConnectionImpl{" +
                "handle=" + handle +
                ", encrypted=" + encrypted +
                '}';
    }
}
