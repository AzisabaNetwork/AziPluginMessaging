package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerMessage implements Message {
    protected final Player player;

    public PlayerMessage(@NotNull Player player) {
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeUTF(player.getUniqueId().toString());
        out.writeBoolean(player.getUsername() != null);
        if (player.getUsername() != null) {
            out.writeUTF(player.getUsername());
        }
    }

    @Contract("_ -> new")
    public static @NotNull PlayerMessage read(@NotNull DataInputStream in) throws IOException {
        UUID uuid = UUID.fromString(in.readUTF());
        String username = null;
        if (in.readBoolean()) {
            username = in.readUTF();
        }
        return new PlayerMessage(new SimplePlayer(uuid, username));
    }
}
