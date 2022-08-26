package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.EmptyMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.EncryptionMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
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

        // Enable encryption to be able to send packet
        sender.setEncrypted(true);
        sender.setEncryptedOnce();
        try {
            // Send response to proxy
            Protocol.P_ENCRYPTION_RESPONSE.sendPacket(sender, EmptyMessage.INSTANCE);
        } finally {
            // Disable encryption because response is not guaranteed to be received on our side
            sender.setEncrypted(false);
        }
    }
}
