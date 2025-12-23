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
 * A message to add permission to a player.
 */
public class ProxyboundNodeMessage extends PlayerWithServerMessage {
    protected final String node;

    public ProxyboundNodeMessage(@NotNull Player player, @Nullable String server, @NotNull String node) {
        super(server, player);
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public ProxyboundNodeMessage(@NotNull Player player, @NotNull String node) {
        super(null, player);
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public @NotNull String getNode() {
        return node;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // yes, we don't need to write the server name because it is extracted from the ServerConnection.
        out.writeUTF(node);
        super.write(out);
    }

    @Contract("null, _ -> fail; _, _ -> new")
    @NotNull
    public static ProxyboundNodeMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        String trueServer = Objects.requireNonNull(server, "server cannot be null");
        String node = in.readUTF();
        return new ProxyboundNodeMessage(SimplePlayer.read(in), trueServer, node);
    }
}
