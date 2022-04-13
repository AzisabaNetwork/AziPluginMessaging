package net.azisaba.azipluginmessaging.api.server;

import org.jetbrains.annotations.NotNull;

public interface ServerInfo {
    /**
     * Returns the name of the server.
     * @return the server name
     */
    @NotNull
    String getName();
}
