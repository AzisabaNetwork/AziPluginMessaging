package net.azisaba.azipluginmessaging.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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

    public class ProxyImpl implements Proxy {
        @Override
        public @NotNull Optional<PacketSender> getPacketSenderForServer(@NotNull String serverName) {
            return AziPluginMessagingVelocity.this.server
                    .getServer(serverName)
                    .map(server -> data -> server.sendPluginMessage(MinecraftChannelIdentifier.from(Protocol.CHANNEL_ID), data));
        }
    }
}
