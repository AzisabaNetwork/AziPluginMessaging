package net.azisaba.azipluginmessaging.api.server;

import net.azisaba.azipluginmessaging.api.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ServerConnection extends PacketSender {
    @NotNull
    Player getPlayer();

    @NotNull
    ServerInfo getServerInfo();
}
