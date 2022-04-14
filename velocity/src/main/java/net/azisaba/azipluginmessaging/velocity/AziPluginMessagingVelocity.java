package net.azisaba.azipluginmessaging.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.PlayerAdapter;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.velocity.entity.PlayerImpl;
import net.azisaba.azipluginmessaging.velocity.server.ServerConnectionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class AziPluginMessagingVelocity implements AziPluginMessaging {
    private final ProxyServer server;
    private final Logger logger;
    private final Proxy proxy;

    public AziPluginMessagingVelocity(@NotNull ProxyServer server, @NotNull org.slf4j.Logger slf4jLogger) {
        this.server = server;
        this.logger = Logger.createByProxy(slf4jLogger);
        this.proxy = new ProxyImpl();
    }

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public @NotNull Proxy getProxy() {
        return proxy;
    }

    @Override
    public @NotNull Server getServer() {
        return new Server() {};
    }

    @Override
    public @NotNull Optional<net.azisaba.azipluginmessaging.api.entity.Player> getPlayer(@NotNull UUID uuid) {
        return server.getPlayer(uuid).map(PlayerImpl::new);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PlayerAdapter<T> getPlayerAdapter(@NotNull Class<T> clazz) {
        if (!Player.class.equals(clazz)) throw new IllegalArgumentException("This environment does not support " + clazz.getTypeName());
        return (PlayerAdapter<T>) (PlayerAdapter<Player>) PlayerImpl::new;
    }

    public static class ProxyImpl implements Proxy {
    }
}
