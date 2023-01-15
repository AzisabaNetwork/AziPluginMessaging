package net.azisaba.azipluginmessaging.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.EnvironmentType;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.PlayerAdapter;
import net.azisaba.azipluginmessaging.api.protocol.PacketQueue;
import net.azisaba.azipluginmessaging.api.util.LuckPermsUtil;
import net.azisaba.azipluginmessaging.api.util.SQLThrowableConsumer;
import net.azisaba.azipluginmessaging.api.util.SQLThrowableFunction;
import net.azisaba.azipluginmessaging.api.yaml.YamlObject;
import net.azisaba.azipluginmessaging.velocity.entity.PlayerImpl;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AziPluginMessagingVelocity implements AziPluginMessaging {
    private final ProxyServer server;
    private final Logger logger;
    private final Proxy proxy;
    private DatabaseConfig databaseConfig;

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

    @Override
    public @NotNull PacketQueue getPacketQueue() {
        return PacketQueue.EMPTY;
    }

    @Override
    public @NotNull EnvironmentType getEnvironmentType() {
        return EnvironmentType.VELOCITY;
    }

    @Contract(pure = true)
    @NotNull
    public static AziPluginMessagingVelocity get() {
        return (AziPluginMessagingVelocity) AziPluginMessagingProvider.get();
    }

    @NotNull
    public DatabaseConfig getDatabaseConfig() {
        return Objects.requireNonNull(databaseConfig, "Did not load database config during initialization");
    }

    public class ProxyImpl implements Proxy {
        @Override
        public void loadConfig(@NotNull YamlObject obj) {
            YamlObject section = obj.getObject("database");
            if (section == null) {
                throw new IllegalArgumentException("Missing database section in config");
            }
            databaseConfig = new DatabaseConfig(section);
        }

        @Contract
        @Override
        public @NotNull CompletableFuture<Void> runPreparedStatement(@NotNull String sql, @NotNull SQLThrowableConsumer<PreparedStatement> action) {
            return DBConnector.runPreparedStatement(sql, action);
        }

        @Contract
        @Override
        public @NotNull <T> CompletableFuture<T> getPreparedStatement(@NotNull String sql, @NotNull SQLThrowableFunction<PreparedStatement, T> action) {
            return DBConnector.getPreparedStatement(sql, action);
        }

        @Override
        public void checkRankAsync(@NotNull UUID uuid) {
            DBConnector.runPreparedStatement("SELECT `rank`, `expires_at`, `clear_prefix_on_expire` FROM `temp_rank` WHERE `player_uuid` = ? AND `expires_at` > 0 AND `expires_at` < ?", ps -> {
                LuckPerms lp = LuckPermsProvider.get();
                User user = lp.getUserManager().getUser(uuid);
                if (user == null) {
                    logger.warn("Failed to check rank for {} because the user data does not exist", uuid);
                    return;
                }
                String username = user.getUsername();
                if (username == null) {
                    logger.warn("Failed to check rank for {} because the username is null for some reason", uuid);
                    return;
                }
                ps.setString(1, uuid.toString());
                ps.setLong(2, System.currentTimeMillis());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String rank = rs.getString("rank");
                    long expiresAt = rs.getLong("expires_at");
                    boolean clearPrefixOnExpire = rs.getBoolean("clear_prefix_on_expire");
                    // remove rank
                    logger.info("Removing rank {} for {} (expired at {})", rank, username, expiresAt);
                    DBConnector.runPreparedStatement("DELETE FROM `temp_rank` WHERE `player_uuid` = ?", ps2 -> {
                        ps2.setString(1, uuid.toString());
                        ps2.executeUpdate();
                    }).join();
                    NodeMap map = user.getData(DataType.NORMAL);
                    Node rankNode = LuckPermsUtil.findParentNode(map, rank, null);
                    if (rankNode == null) {
                        logger.warn("Failed to remove rank {} for {} (already removed)", rank, uuid);
                        return;
                    }
                    map.remove(rankNode);
                    if (clearPrefixOnExpire) {
                        LuckPermsUtil.findAllPrefixNodes(map).forEach(map::remove);
                    }
                    lp.getUserManager().saveUser(user);
                    lp.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
                    lp.getActionLogger().submit(
                            Action.builder()
                                    .targetType(Action.Target.Type.USER)
                                    .timestamp(Instant.now())
                                    .source(new UUID(0L, 0L))
                                    .sourceName("AziPluginMessaging@" + lp.getServerName())
                                    .target(uuid)
                                    .targetName(username)
                                    .description("Removed " + rank + " from " + username + " (expired at " + expiresAt + ", clear prefix: " + clearPrefixOnExpire + ")")
                                    .build());
                }
            });
        }
    }
}
