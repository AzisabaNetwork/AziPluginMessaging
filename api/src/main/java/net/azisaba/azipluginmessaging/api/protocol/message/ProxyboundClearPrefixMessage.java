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

    public ProxyboundClearPrefixMessage(@Nullable String server, @NotNull Player player) {
        super(server, player);
        this.global = "global".equals(server);
    }

    public ProxyboundClearPrefixMessage(boolean global, @NotNull Player player) {
        super(null, player);
        this.global = global;
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // yes, we don't need to write the server name because it is extracted from the ServerConnection.
        out.writeBoolean(global);
        super.write(out);
    }

    @Contract("null, _ -> fail; _, _ -> new")
    @NotNull
    public static ProxyboundClearPrefixMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        String trueServer = Objects.requireNonNull(server, "server cannot be null");
        if (in.readBoolean()) { // global
            trueServer = "global";
        }
        return new ProxyboundClearPrefixMessage(trueServer, SimplePlayer.read(in)); // player
    }
}
