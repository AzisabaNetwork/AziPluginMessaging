package net.azisaba.azipluginmessaging.api;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.PlayerAdapter;
import net.azisaba.azipluginmessaging.api.protocol.PacketQueue;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

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

    @NotNull
    Optional<Player> getPlayer(@NotNull UUID uuid);

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

    /**
     * Returns the packet queue.
     * @return the packet queue
     */
    @NotNull
    PacketQueue getPacketQueue();

    interface Proxy {
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
