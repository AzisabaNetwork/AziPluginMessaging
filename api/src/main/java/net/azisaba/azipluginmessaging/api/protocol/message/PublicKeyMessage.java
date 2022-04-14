package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class PublicKeyMessage implements Message {
    private final PublicKey publicKey;

    public PublicKeyMessage(@NotNull PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @NotNull
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeUTF(EncryptionUtil.encodePublicKey(publicKey));
    }

    @Contract("_ -> new")
    public static @NotNull PublicKeyMessage read(@NotNull DataInputStream in) throws Exception {
        return new PublicKeyMessage(EncryptionUtil.decodePublicKey(in.readUTF()));
    }
}
