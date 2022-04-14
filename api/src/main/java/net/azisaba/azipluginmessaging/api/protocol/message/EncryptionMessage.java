package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class EncryptionMessage implements Message {
    private final String challenge;
    private final PublicKey publicKey;

    public EncryptionMessage(@NotNull String challenge, @NotNull PublicKey publicKey) {
        this.challenge = challenge;
        this.publicKey = publicKey;
    }

    @NotNull
    public String getChallenge() {
        return challenge;
    }

    @NotNull
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeUTF(challenge);
        out.writeUTF(EncryptionUtil.encodePublicKey(publicKey));
    }

    @Contract("_ -> new")
    public static @NotNull EncryptionMessage read(@NotNull DataInputStream in) throws Exception {
        return new EncryptionMessage(in.readUTF(), EncryptionUtil.decodePublicKey(in.readUTF()));
    }
}
