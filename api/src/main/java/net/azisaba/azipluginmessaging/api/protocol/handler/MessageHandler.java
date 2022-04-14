package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

public interface MessageHandler<T extends Message> {
    /**
     * Handles the (decoded) message.
     *
     * @param sender The sender of the message.
     * @param msg    The message.
     * @throws Exception If an exception occurs.
     */
    void handle(@NotNull PacketSender sender, @NotNull T msg) throws Exception;
}
