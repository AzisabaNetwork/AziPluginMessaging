package net.azisaba.azipluginmessaging.api;

import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AziPluginMessaging {
    @NotNull
    Logger getLogger();

    /**
     * Returns the proxy instance.
     * @throws IllegalStateException if the current environment is not a proxy (running minecraft server and such).
     * @return the proxy instance
     */
    @NotNull
    Proxy getProxy();

    /**
     * Returns the single server instance.
     * @throws IllegalStateException if the current environment is not a server (running proxy and such).
     * @return the single server instance
     */
    @NotNull
    Server getServer();

    interface Proxy {
        @NotNull
        default Optional<PacketSender> getPacketSenderForServer(@NotNull String serverName) {
            throw new UnsupportedOperationException("Unsupported in current environment.");
        }
    }

    interface Server {
        @NotNull
        default PacketSender getPacketSender() {
            throw new UnsupportedOperationException("Unsupported in current environment.");
        }
    }
}
