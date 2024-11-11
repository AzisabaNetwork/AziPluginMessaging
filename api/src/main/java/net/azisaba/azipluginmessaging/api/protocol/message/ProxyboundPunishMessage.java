package net.azisaba.azipluginmessaging.api.protocol.message;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import net.azisaba.azipluginmessaging.api.punishment.PunishmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProxyboundPunishMessage extends PlayerWithServerMessage {
    private final Player sender;
    private final PunishmentType type;
    private final String reason;
    private final int time;
    private final TimeUnit unit;

    public ProxyboundPunishMessage(
            @NotNull String server,
            @NotNull Player player,
            @NotNull Player sender,
            @NotNull PunishmentType type,
            @NotNull String reason,
            int time,
            @Nullable TimeUnit unit) {
        super(server, player);
        if (type.name().startsWith("TEMP_")) {
            if (time <= 0 || unit == null) {
                throw new IllegalArgumentException("Time and unit must be set for temporary punishments");
            }
        } else {
            if (time != 0 || unit != null) {
                throw new IllegalArgumentException("Time and unit must not be set for permanent punishments");
            }
        }
        this.sender = Objects.requireNonNull(sender, "sender");
        this.type = Objects.requireNonNull(type, "type");
        this.reason = Objects.requireNonNull(reason, "reason");
        this.time = time;
        this.unit = unit;
    }

    public @NotNull Player getSender() {
        return sender;
    }

    public @NotNull PunishmentType getType() {
        return type;
    }

    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Returns the duration of the punishment. If the punishment is permanent, it returns 0.
     * @return the duration of the punishment
     */
    public int getTime() {
        return unit == null ? 0 : time;
    }

    /**
     * Returns the unit of the duration. If the unit is null, it means the punishment is permanent.
     * @return the unit of the duration
     */
    public @Nullable TimeUnit getUnit() {
        return unit;
    }

    /**
     * Returns whether the punishment is temporary or not.
     * @return whether the punishment is temporary or not
     */
    public boolean isTemporary() {
        return getTime() > 0 && unit != null;
    }

    /**
     * Returns whether the punishment is permanent or not.
     * @return whether the punishment is permanent or not
     */
    public boolean isPermanent() {
        return !isTemporary(); // time == 0 || unit == null
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(sender.getUniqueId().toString());
        out.writeBoolean(sender.getUsername() != null);
        if (sender.getUsername() != null) {
            out.writeUTF(sender.getUsername());
        }
        out.writeUTF(type.name());
        out.writeUTF(reason);
        out.writeInt(time);
        out.writeBoolean(unit != null);
        if (unit != null) {
            out.writeUTF(unit.name());
        }
    }

    @Override
    public String toString() {
        return "ProxyboundPunishMessage{" +
                "sender=" + sender +
                ", type=" + type +
                ", reason='" + reason + '\'' +
                ", time=" + time +
                ", unit=" + unit +
                ", server='" + server + '\'' +
                ", player=" + player +
                '}';
    }

    public static @NotNull ProxyboundPunishMessage read(@NotNull String server, @NotNull DataInputStream in) throws IOException {
        return new ProxyboundPunishMessage(
                server,
                SimplePlayer.read(in),
                SimplePlayer.read(in),
                PunishmentType.valueOf(in.readUTF()),
                in.readUTF(),
                in.readInt(),
                in.readBoolean() ? TimeUnit.valueOf(in.readUTF()) : null
        );
    }
}
