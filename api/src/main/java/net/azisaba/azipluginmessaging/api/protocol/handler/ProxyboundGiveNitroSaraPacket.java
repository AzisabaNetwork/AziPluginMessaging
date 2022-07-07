package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundGiveNitroSaraMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.LuckPermsUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProxyboundGiveNitroSaraPacket implements ProxyMessageHandler<ProxyboundGiveNitroSaraMessage> {
    private static final String NITRO_GROUP_NAME = "nitro";

    @Override
    public @NotNull ProxyboundGiveNitroSaraMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        Player player = SimplePlayer.read(in);
        int time = in.readInt();
        TimeUnit unit = TimeUnit.valueOf(in.readUTF());
        return new ProxyboundGiveNitroSaraMessage(player, time, unit);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundGiveNitroSaraMessage msg) throws SQLException {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User " + msg.getPlayer().getUniqueId() + " could not be found in the LuckPerms database.");
        }
        NodeMap map = user.getData(DataType.NORMAL);
        Node node = LuckPermsUtil.findParentNode(map, NITRO_GROUP_NAME, null);
        if (node == null) {
            LuckPermsUtil.addGroup(map, NITRO_GROUP_NAME, null, -1);
        }
        Node changeSara = LuckPermsUtil.findParentNode(map, "change" + NITRO_GROUP_NAME, null);
        if (changeSara == null) {
            LuckPermsUtil.addGroup(map, "change" + NITRO_GROUP_NAME, null, -1);
        }
        // add time
        long time = msg.getUnit().toMillis(msg.getTime());
        long expiresAt = System.currentTimeMillis() + time;
        Logger.getCurrentLogger().info("Adding time of rank " + NITRO_GROUP_NAME + " to " + user.getUsername() + " for " + msg.getTime() + " " + msg.getUnit().name().toLowerCase());
        AziPluginMessagingProvider.get().getProxy().runPreparedStatement(
                "INSERT INTO `temp_rank` (`player_uuid`, `rank`, `expires_at`, `clear_prefix_on_expire`) VALUES (?, ?, ?, 1) ON DUPLICATE KEY UPDATE `expires_at` = `expires_at` + ?",
                (ps) -> {
                    ps.setString(1, msg.getPlayer().getUniqueId().toString());
                    ps.setString(2, NITRO_GROUP_NAME);
                    ps.setLong(3, expiresAt);
                    ps.setLong(4, time);
                    ps.executeUpdate();
                }).join();
        AziPluginMessagingProvider.get().getProxy().runPreparedStatement(
                "INSERT INTO `temp_rank` (`player_uuid`, `rank`, `expires_at`, `clear_prefix_on_expire`) VALUES (?, ?, ?, 1) ON DUPLICATE KEY UPDATE `expires_at` = `expires_at` + ?",
                (ps) -> {
                    ps.setString(1, msg.getPlayer().getUniqueId().toString());
                    ps.setString(2, "change" + NITRO_GROUP_NAME);
                    ps.setLong(3, expiresAt);
                    ps.setLong(4, time);
                    ps.executeUpdate();
                }).join();
        String username = user.getUsername();
        api.getUserManager().saveUser(user).join();
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
        api.getActionLogger().submit(
                Action.builder()
                        .targetType(Action.Target.Type.USER)
                        .timestamp(Instant.now())
                        .source(new UUID(0L, 0L))
                        .sourceName("AziPluginMessaging@" + api.getServerName())
                        .target(msg.getPlayer().getUniqueId())
                        .targetName(username)
                        .description("Added nitro sara to " + username + " (+" + msg.getTime() + " " + msg.getUnit().name().toLowerCase() + ")")
                        .build());
    }
}
