package net.azisaba.azipluginmessaging.api.server;

import net.azisaba.azipluginmessaging.api.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;

/**
 * Represents a connection between a server.
 */
public interface ServerConnection extends PacketSender {
    /**
     * Returns the player associated with this connection.
     * @return the player
     */
    @NotNull
    Player getPlayer();

    /**
     * Returns the server info.
     * @return the server info
     */
    @NotNull
    ServerInfo getServerInfo();

    /**
     * Sets the key pair for encryption.
     * @param keyPair the key pair
     */
    void setKeyPair(@NotNull KeyPair keyPair);
}
