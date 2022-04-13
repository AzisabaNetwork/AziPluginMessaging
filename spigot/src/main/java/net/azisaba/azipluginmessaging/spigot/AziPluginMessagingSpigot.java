package net.azisaba.azipluginmessaging.spigot;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.PlayerAdapter;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AziPluginMessagingSpigot implements AziPluginMessaging {
    private final Logger logger;
    private final Server server;

    public AziPluginMessagingSpigot(@NotNull SpigotPlugin plugin) {
        this.logger = Logger.createFromJavaLogger(plugin.getLogger());
        this.server = new ServerImpl();
    }

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public @NotNull Proxy getProxy() {
        return new Proxy() {};
    }

    @Override
    public @NotNull Server getServer() {
        return server;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PlayerAdapter<T> getPlayerAdapter(@NotNull Class<T> clazz) {
        if (!Player.class.equals(clazz)) throw new IllegalArgumentException("This environment does not support " + clazz.getTypeName());
        return (PlayerAdapter<T>) (PlayerAdapter<Player>) PlayerImpl::new;
    }

    public static class ServerImpl implements Server {
        @Override
        public @NotNull PacketSender getPacketSender() {
            return data -> {
                Optional<? extends org.bukkit.entity.Player> p = Bukkit.getOnlinePlayers().stream().findAny();
                if (!p.isPresent()) {
                    return false;
                }
                PlayerImpl.sendPacketFromPlayer(p.get(), data);
                return true;
            };
        }
    }
}
