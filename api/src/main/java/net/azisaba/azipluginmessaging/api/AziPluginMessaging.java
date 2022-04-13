package net.azisaba.azipluginmessaging.api;

import net.azisaba.azipluginmessaging.api.entity.PlayerAdapter;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AziPluginMessaging {
    /**
     * Returns the logger instance.
     * @return the logger
     */
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

    /**
     * Returns the player adapter for class.
     * <p>Generally, class should be one of these (depending on the environment):
     * <ul>
     *     <li>org.bukkit.entity.Player</li>
     *     <li>com.velocitypowered.api.proxy.Player</li>
     * </ul>
     * @param clazz the platform dependent player class
     * @return the player adapter
     * @param <T> the player class
     */
    <T> PlayerAdapter<T> getPlayerAdapter(@NotNull Class<T> clazz);

    interface Proxy {
        /**
         * Returns the packet sender instance for provided server.
         * @param serverName the server name
         * @return the packet sender instance
         */
        @NotNull
        default Optional<PacketSender> getPacketSenderForServer(@NotNull String serverName) {
            throw new UnsupportedOperationException("Unsupported in current environment.");
        }
    }

    interface Server {
        /**
         * Returns the static packet sender instance.
         * @return the packet sender
         */
        @NotNull
        default PacketSender getPacketSender() {
            throw new UnsupportedOperationException("Unsupported in current environment.");
        }
    }
}
