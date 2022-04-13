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

public class SetRankMessage extends PlayerWithServerMessage {
    protected final String rank;

    public SetRankMessage(@NotNull String server, @NotNull String rank, @NotNull Player player) {
        super(server, player);
        this.rank = Objects.requireNonNull(rank, "rank cannot be null");
    }

    public SetRankMessage(@NotNull String rank, @NotNull Player player) {
        this("global", rank, player);
    }

    @NotNull
    public String getRank() {
        return rank;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // yes, we don't need to write the server name because it is extracted from the ServerConnection.
        out.writeUTF(rank);
        super.write(out);
    }

    @Contract("null, _ -> fail; _, _ -> new")
    @NotNull
    public static SetRankMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        return new SetRankMessage(Objects.requireNonNull(server, "server cannot be null"), in.readUTF(), SimplePlayer.read(in));
    }
}
