package net.azisaba.azipluginmessaging.velocity.server;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.server.ServerInfo;
import net.azisaba.azipluginmessaging.velocity.entity.PlayerImpl;
import org.jetbrains.annotations.NotNull;

public class ServerConnectionImpl implements ServerConnection {
    private final com.velocitypowered.api.proxy.ServerConnection handle;

    public ServerConnectionImpl(@NotNull com.velocitypowered.api.proxy.ServerConnection handle) {
        this.handle = handle;
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
        return getHandle().sendPluginMessage(MinecraftChannelIdentifier.from(Protocol.CHANNEL_ID), data);
    }

    @NotNull
    @Override
    public String toString() {
        return "ServerConnectionImpl{handle=" + handle + '}';
    }
}
