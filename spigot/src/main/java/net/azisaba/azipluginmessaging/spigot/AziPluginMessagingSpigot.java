package net.azisaba.azipluginmessaging.spigot;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.EnvironmentType;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.PlayerAdapter;
import net.azisaba.azipluginmessaging.api.protocol.PacketQueue;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import net.azisaba.azipluginmessaging.spigot.protocol.SimplePacketQueue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AziPluginMessagingSpigot implements AziPluginMessaging {
    private final PacketQueue packetQueue = new SimplePacketQueue();
    private final Logger logger;
    private final ServerImpl server;

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
    public @NotNull ServerImpl getServer() {
        return server;
    }

    @Override
    public @NotNull Optional<net.azisaba.azipluginmessaging.api.entity.Player> getPlayer(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return Optional.empty();
        return Optional.of(PlayerImpl.of(player));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PlayerAdapter<T> getPlayerAdapter(@NotNull Class<T> clazz) {
        if (!Player.class.equals(clazz)) throw new IllegalArgumentException("This environment does not support " + clazz.getTypeName());
        return (PlayerAdapter<T>) (PlayerAdapter<Player>) PlayerImpl::of;
    }

    @Override
    public @NotNull PacketQueue getPacketQueue() {
        return packetQueue;
    }

    @Override
    public @NotNull EnvironmentType getEnvironmentType() {
        return EnvironmentType.SPIGOT;
    }

    public static class ServerImpl implements Server {
        @Override
        public @NotNull PacketSender getPacketSender() {
            // prefer encrypted players (that is, player who is connected to the proxy where AziPluginMessaging is installed)
            List<PlayerImpl> players = Bukkit.getOnlinePlayers()
                    .stream()
                    .map(PlayerImpl::of)
                    .collect(Collectors.toList());
            if (players.size() == 0) throw new IllegalArgumentException("No player is online.");
            Optional<PlayerImpl> encryptedPlayer = players.stream().filter(PlayerImpl::isEncrypted).findAny();
            return encryptedPlayer.orElseGet(() -> players.get(0));
        }

        public @Nullable PacketSender getPacketSenderOrNull() {
            try {
                PacketSender sender = getPacketSender();
                if (sender.isEncrypted()) {
                    return sender;
                }
            } catch (RuntimeException ignored) {}
            return null;
        }
    }
}
