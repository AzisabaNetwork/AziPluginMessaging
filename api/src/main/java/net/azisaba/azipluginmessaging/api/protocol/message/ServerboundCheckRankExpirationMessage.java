package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ApiStatus.Internal // this packet WILL change
public class ServerboundCheckRankExpirationMessage extends PlayerMessage {
    private final String rank;
    private final long expiresAt;

    public ServerboundCheckRankExpirationMessage(@NotNull Player player, @NotNull String rank, long expiresAt) {
        super(player);
        this.rank = Objects.requireNonNull(rank, "rank");
        this.expiresAt = expiresAt;
    }

    /**
     * Returns the rank which the server has requested.
     * @return the rank
     */
    @NotNull
    public String getRank() {
        return rank;
    }

    /**
     * Returns the expiration time of the player's rank in milliseconds.<br>
     * Special cases:<br>
     * <ul>
     *     <li>Returns 0 if the rank never expires</li>
     *     <li>Returns -1 if the player doesn't have the temporary rank.</li>
     * </ul>
     * @return expiration time
     */
    public long getExpiresAt() {
        return expiresAt;
    }

    @NotNull
    public Instant getExpiresAtInstant() {
        return Instant.ofEpochMilli(expiresAt);
    }

    /**
     * @deprecated this method does not make any sense; this method should never be used.
     */
    @Deprecated
    public long getExpiresAt(@NotNull TimeUnit unit) {
        return unit.convert(expiresAt, TimeUnit.MILLISECONDS);
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(rank);
        out.writeLong(expiresAt);
    }

    public static @NotNull ServerboundCheckRankExpirationMessage read(@NotNull DataInputStream in) throws IOException {
        Player player = SimplePlayer.read(in);
        String rank = in.readUTF();
        long expiresAt = in.readLong();
        return new ServerboundCheckRankExpirationMessage(player, rank, expiresAt);
    }
}
