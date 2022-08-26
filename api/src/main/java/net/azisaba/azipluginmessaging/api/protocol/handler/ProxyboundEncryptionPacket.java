package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.EncryptionMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ProxyboundEncryptionPacket implements ProxyMessageHandler<EncryptionMessage> {
    @Override
    public @NotNull EncryptionMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        try {
            return EncryptionMessage.read(in);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull EncryptionMessage msg) throws Exception {
        // Set public key of the server for encryption (note that this does not set the encrypted flag)
        sender.setRemotePublicKey(msg.getPublicKey());

        if (sender instanceof ServerConnection) {
            // generate keypair before sending to the server
            ((ServerConnection) sender).setKeyPair(EncryptionUtil.generateKeyPair(2048));
        }

        // Send our public key to the server
        if (!Protocol.S_ENCRYPTION.sendPacket(sender, new EncryptionMessage(msg.getChallenge(), sender.getKeyPair().getPublic()))) {
            Logger.getCurrentLogger().warn("Failed to send public key to the server " + sender);
        }

        // Enable encryption here, otherwise we will not be able to receive the encrypted response
        sender.setEncryptedOnce();
    }
}
