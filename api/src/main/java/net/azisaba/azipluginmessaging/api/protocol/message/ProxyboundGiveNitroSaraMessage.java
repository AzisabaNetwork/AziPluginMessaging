package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProxyboundGiveNitroSaraMessage extends PlayerMessage {
    private final int time;
    private final TimeUnit unit;

    public ProxyboundGiveNitroSaraMessage(@NotNull Player player, int time, @NotNull TimeUnit unit) {
        super(player);
        this.time = time;
        this.unit = unit;
    }

    public int getTime() {
        return time;
    }

    @NotNull
    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        super.write(out);
        out.writeInt(time);
        out.writeUTF(unit.name());
    }
}
