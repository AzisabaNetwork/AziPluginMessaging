package net.azisaba.azipluginmessaging.api.server;

import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

/**
 * Represents an object that can send packet to target.
 */
public interface PacketSender extends Connection {
    /**
     * Attempt to send a packet to the target.
     * @param data the data
     * @return true if the packet was sent successfully; false otherwise
     */
    boolean sendPacket(byte @NotNull [] data);

    void setRemotePublicKey(@NotNull PublicKey publicKey);
}
