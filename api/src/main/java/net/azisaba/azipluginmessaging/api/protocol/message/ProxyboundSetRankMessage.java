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

/**
 * A message that sets the rank of a player.
 */
public class ProxyboundSetRankMessage extends PlayerWithServerMessage {
    protected final String rank;

    public ProxyboundSetRankMessage(@Nullable String server, @NotNull String rank, @NotNull Player player) {
        super(server, player);
        this.rank = Objects.requireNonNull(rank, "rank cannot be null");
    }

    public ProxyboundSetRankMessage(@NotNull String rank, @NotNull Player player) {
        this(null, rank, player);
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
    public static ProxyboundSetRankMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        return new ProxyboundSetRankMessage(Objects.requireNonNull(server, "server cannot be null"), in.readUTF(), SimplePlayer.read(in));
    }
}
