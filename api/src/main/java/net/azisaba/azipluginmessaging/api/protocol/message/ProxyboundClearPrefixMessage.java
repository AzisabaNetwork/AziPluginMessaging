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
 * A message that clears the prefix of a player.
 */
public class ProxyboundClearPrefixMessage extends PlayerWithServerMessage {
    protected final boolean global;
    protected final boolean all;

    public ProxyboundClearPrefixMessage(@NotNull Player player, @Nullable String server, boolean all) {
        super(server, player);
        this.global = "global".equals(server);
        this.all = all;
    }

    public ProxyboundClearPrefixMessage(@NotNull Player player, boolean global, boolean all) {
        super(null, player);
        this.global = global;
        this.all = all;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isAll() {
        return all;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // yes, we don't need to write the server name because it is extracted from the ServerConnection.
        out.writeBoolean(global);
        out.writeBoolean(all);
        super.write(out);
    }

    @Contract("null, _ -> fail; _, _ -> new")
    @NotNull
    public static ProxyboundClearPrefixMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        String trueServer = Objects.requireNonNull(server, "server cannot be null");
        if (in.readBoolean()) { // global
            trueServer = "global";
        }
        boolean all = in.readBoolean();
        return new ProxyboundClearPrefixMessage(SimplePlayer.read(in), trueServer, all); // player
    }
}
