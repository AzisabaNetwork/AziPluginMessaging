package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PublicKeyMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.Constants;
import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;

public class ProxyboundEncryptionPacket implements ProxyMessageHandler<PublicKeyMessage> {
    @Override
    public @NotNull PublicKeyMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) {
        try {
            return PublicKeyMessage.read(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull PublicKeyMessage msg) throws Exception {
        // Set public key of the server for encryption (note that this does not set the encrypted flag)
        sender.setRemotePublicKey(msg.getPublicKey());

        if (sender instanceof ServerConnection) {
            // generate keypair before sending to the server
            ((ServerConnection) sender).setKeyPair(EncryptionUtil.generateKeyPair(2048));
        }

        // Send our public key to the server
        if (!Protocol.S_ENCRYPTION.sendPacket(sender, new PublicKeyMessage(sender.getKeyPair().getPublic()))) {
            Logger.getCurrentLogger().warn("Failed to send public key to the server " + sender);
        }

        // Enable encryption
        sender.setEncrypted(true);

        if (Constants.DEBUG) {
            Logger.getCurrentLogger().info("Encryption enabled for " + sender);
        }
    }
}