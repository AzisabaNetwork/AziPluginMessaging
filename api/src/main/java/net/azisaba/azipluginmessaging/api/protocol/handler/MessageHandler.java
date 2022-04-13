package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public interface MessageHandler<T extends Message> {
    @NotNull
    T read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException;

    void handle(@NotNull T msg) throws Exception;
}
