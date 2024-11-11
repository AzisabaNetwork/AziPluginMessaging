package net.azisaba.azipluginmessaging.api.entity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Simple instance of {@link Player}. It lacks most of the features, and for example, you can't send messages to this
 * instance.
 */
public final class SimplePlayer implements Player {
    private final UUID uuid;
    private final String username;

    public SimplePlayer(@NotNull UUID uuid, @Nullable String username) {
        Objects.requireNonNull(uuid);
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @Nullable String getUsername() {
        return username;
    }

    /**
     * no-op method.
     * @param message the message
     */
    @Override
    public void sendMessage(@NotNull String message) {
        // no-op
    }

    @Override
    public boolean isChallengeEquals(@NotNull String challenge) {
        return false;
    }

    @Override
    public String toString() {
        return "SimplePlayer{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                '}';
    }

    @Contract("_ -> new")
    @NotNull
    public static SimplePlayer read(@NotNull DataInputStream in) throws IOException {
        UUID uuid = UUID.fromString(in.readUTF());
        String username = null;
        if (in.readBoolean()) {
            username = in.readUTF();
        }
        return new SimplePlayer(uuid, username);
    }
}
