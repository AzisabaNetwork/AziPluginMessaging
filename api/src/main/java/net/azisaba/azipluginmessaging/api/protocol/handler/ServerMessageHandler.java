package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Represents a message handler which is responsible for decoding and handling the message.
 * @param <T> the message type
 */
public interface ServerMessageHandler<T extends Message> extends MessageHandler<T> {
    /**
     * Decodes the message from the stream.
     * @param in The stream.
     * @return The decoded message.
     * @throws IOException If an I/O error occurs.
     */
    @NotNull
    T read(@NotNull DataInputStream in) throws IOException;
}
