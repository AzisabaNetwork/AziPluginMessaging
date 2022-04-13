package net.azisaba.azipluginmessaging.api.server;

import org.jetbrains.annotations.NotNull;

public interface PacketSender {
    /**
     * Attempt to send a packet to the server.
     * @param data the data
     * @return true if the packet was sent successfully; false otherwise
     */
    boolean sendPacket(byte @NotNull [] data);
}
