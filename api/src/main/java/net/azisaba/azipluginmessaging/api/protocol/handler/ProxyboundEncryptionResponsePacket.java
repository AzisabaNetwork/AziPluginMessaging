package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.EmptyMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ProxyboundEncryptionResponsePacket implements ProxyMessageHandler<EmptyMessage> {
    @Override
    public @NotNull EmptyMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return EmptyMessage.INSTANCE;
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull EmptyMessage msg) throws Exception {
        // Enable encryption
        sender.setEncrypted(true);

        if (AziPluginMessagingConfig.debug) {
            Logger.getCurrentLogger().info("Encryption enabled for " + sender);
        }

        // Send response to server to set the "encrypted" flag to true
        Protocol.S_ENCRYPTION_RESPONSE.sendPacket(sender, EmptyMessage.INSTANCE);
    }
}
