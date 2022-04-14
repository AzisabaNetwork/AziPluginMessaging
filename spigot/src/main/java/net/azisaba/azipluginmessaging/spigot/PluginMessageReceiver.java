package net.azisaba.azipluginmessaging.spigot;

import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Protocol.handleServerSide(new PlayerImpl(player), message);
    }
}
