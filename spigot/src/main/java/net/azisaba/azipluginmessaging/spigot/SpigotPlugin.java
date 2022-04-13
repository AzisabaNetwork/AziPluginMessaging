package net.azisaba.azipluginmessaging.spigot;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProviderProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.spigot.command.AziPluginMessagingCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpigotPlugin extends JavaPlugin implements PacketSender {
    public static SpigotPlugin plugin;

    @Override
    public void onLoad() {
        plugin = this;
        AziPluginMessaging api = new AziPluginMessagingSpigot(this);
        AziPluginMessagingProviderProvider.register(api);
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(Bukkit.getPluginCommand("azipluginmessaging")).setExecutor(new AziPluginMessagingCommand());
        try {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, Protocol.LEGACY_CHANNEL_ID);
        } catch (IllegalArgumentException ignored) {}
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Protocol.CHANNEL_ID);
    }

    @Override
    public boolean sendPacket(byte @NotNull [] data) {
        return AziPluginMessagingProvider.get().getServer().getPacketSender().sendPacket(data);
    }
}
