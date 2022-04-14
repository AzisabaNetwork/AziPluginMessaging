package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.message.PublicKeyMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;

public class ServerboundEncryptionPacket implements ServerMessageHandler<PublicKeyMessage> {
    @Override
    public @NotNull PublicKeyMessage read(@NotNull DataInputStream in) {
        try {
            return PublicKeyMessage.read(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull PublicKeyMessage msg) throws Exception {
        // Set the public key from the proxy
        sender.setRemotePublicKey(msg.getPublicKey());

        // Enable encryption
        sender.setEncrypted(true);

        if (Constants.DEBUG) {
            Logger.getCurrentLogger().info("Encryption enabled for " + sender);
        }
    }
}
