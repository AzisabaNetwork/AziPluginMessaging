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
 * A message that sets the prefix of a player.
 */
public class ProxyboundSetPrefixMessage extends PlayerWithServerMessage {
    protected final boolean global;
    protected final String prefix;

    public ProxyboundSetPrefixMessage(@NotNull Player player, @Nullable String server, @NotNull String prefix) {
        super(server, player);
        this.global = "global".equals(server);
        this.prefix = Objects.requireNonNull(prefix, "prefix cannot be null");
    }

    public ProxyboundSetPrefixMessage(@NotNull Player player, boolean global, @NotNull String prefix) {
        super(null, player);
        this.global = global;
        this.prefix = Objects.requireNonNull(prefix, "prefix cannot be null");
    }

    @Contract("_, _, _ -> new")
    public static @NotNull ProxyboundSetPrefixMessage createFromServerside(@NotNull Player player, boolean global, @NotNull String prefix) {
        return new ProxyboundSetPrefixMessage(player, global, prefix);
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // yes, we don't need to write the server name because it is extracted from the ServerConnection.
        out.writeBoolean(global);
        out.writeUTF(prefix);
        super.write(out);
    }

    @Contract("null, _ -> fail; _, _ -> new")
    @NotNull
    public static ProxyboundSetPrefixMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        String trueServer = Objects.requireNonNull(server, "server cannot be null");
        if (in.readBoolean()) { // global
            trueServer = "global";
        }
        String prefix = in.readUTF();
        return new ProxyboundSetPrefixMessage(SimplePlayer.read(in), trueServer, prefix);
    }
}
