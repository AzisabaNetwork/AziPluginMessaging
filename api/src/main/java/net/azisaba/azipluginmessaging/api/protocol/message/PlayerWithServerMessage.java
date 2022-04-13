package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class PlayerWithServerMessage extends PlayerMessage {
    protected final String server;

    public PlayerWithServerMessage(@NotNull String server, @NotNull Player player) {
        super(player);
        this.server = server;
    }

    public PlayerWithServerMessage(@NotNull Player player) {
        this("global", player);
    }

    @NotNull
    public String getServer() {
        return server;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // yes, we don't need to write the server name because it is extracted from the ServerConnection.
        super.write(out);
    }

    @Contract("null, _ -> fail; _, _ -> new")
    @NotNull
    public static PlayerWithServerMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        UUID uuid = UUID.fromString(in.readUTF());
        String username = null;
        if (in.readBoolean()) {
            username = in.readUTF();
        }
        return new PlayerWithServerMessage(Objects.requireNonNull(server, "server cannot be null"), new SimplePlayer(uuid, username));
    }
}
