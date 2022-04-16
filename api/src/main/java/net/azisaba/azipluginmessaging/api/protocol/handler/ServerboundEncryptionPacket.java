package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.message.EncryptionMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ServerboundEncryptionPacket implements ServerMessageHandler<EncryptionMessage> {
    @Override
    public @NotNull EncryptionMessage read(@NotNull DataInputStream in) throws IOException {
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
        if (sender instanceof Player && !((Player) sender).isChallengeEquals(msg.getChallenge())) {
            throw new RuntimeException("Challenge token does not match.");
        }

        // Set the public key from the proxy
        sender.setRemotePublicKey(msg.getPublicKey());

        // Enable encryption
        sender.setEncrypted(true);

        if (Constants.DEBUG) {
            Logger.getCurrentLogger().info("Encryption enabled for " + sender);
        }

        AziPluginMessagingProvider.get().getPacketQueue().flush(sender);
    }
}
