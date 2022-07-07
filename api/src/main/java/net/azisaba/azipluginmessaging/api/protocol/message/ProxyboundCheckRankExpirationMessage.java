package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

@ApiStatus.Internal // this packet WILL change
public class ProxyboundCheckRankExpirationMessage extends PlayerMessage {
    private final String rank;

    public ProxyboundCheckRankExpirationMessage(@NotNull Player player, @NotNull String rank) {
        super(player);
        this.rank = Objects.requireNonNull(rank, "rank");
    }

    /**
     * Returns the requested rank.
     * @return the rank
     */
    @NotNull
    public String getRank() {
        return rank;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(rank);
    }

    public static @NotNull ProxyboundCheckRankExpirationMessage read(@NotNull DataInputStream in) throws IOException {
        Player player = SimplePlayer.read(in);
        String rank = in.readUTF();
        return new ProxyboundCheckRankExpirationMessage(player, rank);
    }
}
