package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class ProxyboundGiveSaraMessage extends PlayerMessage {
    private final int amount;

    public ProxyboundGiveSaraMessage(int amount, @NotNull Player player) {
        super(player);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeInt(amount);
        super.write(out);
    }
}
