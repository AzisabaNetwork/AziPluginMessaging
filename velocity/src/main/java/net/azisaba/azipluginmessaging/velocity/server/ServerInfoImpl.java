package net.azisaba.azipluginmessaging.velocity.server;

import net.azisaba.azipluginmessaging.api.server.ServerInfo;
import org.jetbrains.annotations.NotNull;

public class ServerInfoImpl implements ServerInfo {
    private final com.velocitypowered.api.proxy.server.ServerInfo handle;

    public ServerInfoImpl(@NotNull com.velocitypowered.api.proxy.server.ServerInfo handle) {
        this.handle = handle;
    }

    @NotNull
    public com.velocitypowered.api.proxy.server.ServerInfo getHandle() {
        return handle;
    }

    @Override
    public @NotNull String getName() {
        return getHandle().getName();
    }
}
