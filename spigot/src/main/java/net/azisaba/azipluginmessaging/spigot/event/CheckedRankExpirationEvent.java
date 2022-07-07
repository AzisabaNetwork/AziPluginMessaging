package net.azisaba.azipluginmessaging.spigot.event;

import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundCheckRankExpirationMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CheckedRankExpirationEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ServerboundCheckRankExpirationMessage msg;

    public CheckedRankExpirationEvent(@NotNull ServerboundCheckRankExpirationMessage msg) {
        this.msg = msg;
    }

    @NotNull
    public ServerboundCheckRankExpirationMessage getMsg() {
        return msg;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused") // used by spigot
    @Contract(pure = true)
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
