package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundActionResponseMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Sends the arbitrary message to a player.
 */
public class ServerboundActionResponsePacket implements ServerMessageHandler<ServerboundActionResponseMessage> {
    @NotNull
    @Override
    public ServerboundActionResponseMessage read(@NotNull DataInputStream in) throws IOException {
        UUID uuid = UUID.fromString(in.readUTF());
        String message = in.readUTF();
        return new ServerboundActionResponseMessage(uuid, message);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ServerboundActionResponseMessage msg) throws Exception {
        AziPluginMessagingProvider.get()
                .getPlayer(msg.getUniqueId())
                .ifPresent(player -> player.sendMessage(msg.getMessage()));
    }
}
