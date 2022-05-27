package net.azisaba.azipluginmessaging.api.protocol.message;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ServerboundActionResponseMessage implements Message {
    private final UUID uuid;
    private final String message;

    /**
     * Creates a new instance.
     * @param uuid The UUID of the player.
     * @param message The message (may contain \u00a7).
     */
    public ServerboundActionResponseMessage(@NotNull UUID uuid, @NotNull String message) {
        this.uuid = uuid;
        this.message = message;
    }

    /**
     * Gets the UUID of the player.
     * @return the uuid
     */
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Gets the message.
     * @return the message
     */
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
