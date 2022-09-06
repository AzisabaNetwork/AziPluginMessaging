package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProxyboundToggleSaraShowMessage extends PlayerWithServerMessage {
    private final Set<Long> groups;

    public ProxyboundToggleSaraShowMessage(@Nullable String server, @NotNull Player player, @NotNull Set<Long> groups) {
        super(server, player);
        this.groups = groups;
    }

    public ProxyboundToggleSaraShowMessage(@NotNull Player player, @NotNull Set<Long> groups) {
        super("global", player);
        this.groups = groups;
    }

    @NotNull
    public Set<Long> getGroups() {
        return groups;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        super.write(out);
        out.writeInt(groups.size());
        for (long group : groups) {
            out.writeLong(group);
        }
    }

    @Contract("_, _ -> new")
    @NotNull
    public static ProxyboundToggleSaraShowMessage read(@Nullable String server, @NotNull DataInputStream in) throws IOException {
        Player player = SimplePlayer.read(in);
        if (in.available() == 0) {
            // backward compatibility
            return new ProxyboundToggleSaraShowMessage(server, player, Collections.emptySet());
        }
        int size = in.readInt();
        Set<Long> groups = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            groups.add(in.readLong());
        }
        return new ProxyboundToggleSaraShowMessage(server, player, groups);
    }
}
