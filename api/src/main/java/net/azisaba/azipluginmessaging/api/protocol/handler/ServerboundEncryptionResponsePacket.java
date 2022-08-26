package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.message.EmptyMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ServerboundEncryptionResponsePacket implements ServerMessageHandler<EmptyMessage> {
    @Override
    public @NotNull EmptyMessage read(@NotNull DataInputStream in) throws IOException {
        return EmptyMessage.INSTANCE;
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull EmptyMessage msg) throws Exception {
        // Enable encryption
        sender.setEncrypted(true);

        if (AziPluginMessagingConfig.debug) {
            Logger.getCurrentLogger().info("Encryption enabled for " + sender);
        }

        AziPluginMessagingProvider.get().getPacketQueue().flush(sender);
    }
}
