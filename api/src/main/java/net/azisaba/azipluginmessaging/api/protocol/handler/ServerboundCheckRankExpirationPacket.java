package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundCheckRankExpirationMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ServerboundCheckRankExpirationPacket implements ServerMessageHandler<ServerboundCheckRankExpirationMessage> {
    @Override
    public @NotNull ServerboundCheckRankExpirationMessage read(@NotNull DataInputStream in) throws IOException {
        return ServerboundCheckRankExpirationMessage.read(in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ServerboundCheckRankExpirationMessage msg) throws Exception {
        AziPluginMessagingProvider.get().getServer().handle(msg);
    }
}
