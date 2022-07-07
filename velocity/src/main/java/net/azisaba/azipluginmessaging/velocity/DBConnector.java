package net.azisaba.azipluginmessaging.velocity;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.azisaba.azipluginmessaging.api.util.SQLThrowableConsumer;
import net.azisaba.azipluginmessaging.api.util.SQLThrowableFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DBConnector {
    private static @Nullable HikariDataSource dataSource;

    /**
     * Initializes the data source and pool.
     */
    public static void init() {
        new Driver();
        HikariConfig config = new HikariConfig();
        if (AziPluginMessagingVelocity.get().getDatabaseConfig().driver() != null) {
            config.setDriverClassName(AziPluginMessagingVelocity.get().getDatabaseConfig().driver());
        }
        config.setJdbcUrl(AziPluginMessagingVelocity.get().getDatabaseConfig().toUrl());
        config.setUsername(AziPluginMessagingVelocity.get().getDatabaseConfig().username());
        config.setPassword(AziPluginMessagingVelocity.get().getDatabaseConfig().password());
        config.setDataSourceProperties(AziPluginMessagingVelocity.get().getDatabaseConfig().properties());
        dataSource = new HikariDataSource(config);
        createTables();
    }

    public static void createTables() {
        runPreparedStatement("CREATE TABLE IF NOT EXISTS `temp_rank` (\n" +
                "  `player_uuid` VARCHAR(36) NOT NULL,\n" +
                "  `rank` VARCHAR(128) NOT NULL DEFAULT 0,\n" +
                "  `expires_at` BIGINT NOT NULL DEFAULT 0,\n" +
                "  `clear_prefix_on_expire` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                "  UNIQUE KEY `pair` (`player_uuid`, `rank`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;", PreparedStatement::execute).join();
    }

    /**
     * Returns the data source. Throws an exception if the data source is not initialized using {@link #init()}.
     * @return the data source
     * @throws NullPointerException if the data source is not initialized using {@link #init()}
     */
    @Contract(pure = true)
    @NotNull
    public static HikariDataSource getDataSource() {
        return Objects.requireNonNull(dataSource, "#init was not called");
    }

    @NotNull
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Contract(pure = true)
    public static <R> R use(@NotNull SQLThrowableFunction<Connection, R> action) throws SQLException {
        try (Connection connection = getConnection()) {
            return action.apply(connection);
        }
    }

    @Contract(pure = true)
    public static void use(@NotNull SQLThrowableConsumer<Connection> action) throws SQLException {
        try (Connection connection = getConnection()) {
            action.accept(connection);
        }
    }

    @Contract
    public static @NotNull CompletableFuture<Void> runPreparedStatement(@Language("SQL") @NotNull String sql, @NotNull SQLThrowableConsumer<PreparedStatement> action) {
        return CompletableFuture.runAsync(() -> {
            try {
                use(connection -> {
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        action.accept(statement);
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Contract(pure = true)
    public static <R> R getPreparedStatement(@Language("SQL") @NotNull String sql, @NotNull SQLThrowableFunction<PreparedStatement, R> action) throws SQLException {
        return use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                return action.apply(statement);
            }
        });
    }

    @Contract(pure = true)
    public static void useStatement(@NotNull SQLThrowableConsumer<Statement> action) throws SQLException {
        use(connection -> {
            try (Statement statement = connection.createStatement()) {
                action.accept(statement);
            }
        });
    }

    /**
     * Closes the data source if it is initialized.
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
