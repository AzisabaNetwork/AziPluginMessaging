package net.azisaba.azipluginmessaging.api.protocol.message;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ServerboundActionResponseMessage implements Message {
    private final UUID uuid;
    private final String message;

    public ServerboundActionResponseMessage(@NotNull UUID uuid, @NotNull String message) {
        this.uuid = uuid;
        this.message = message;
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeUTF(uuid.toString());
        out.writeUTF(message);
    }
}
