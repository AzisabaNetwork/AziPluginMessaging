package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerWithServerMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundActionResponseMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.Constants;
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
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ProxyboundToggleSaraShowPacket implements ProxyMessageHandler<PlayerWithServerMessage> {
    @Override
    public @NotNull PlayerWithServerMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        String serverName = server.getServerInfo().getName();
        String s = AziPluginMessagingConfig.saraShowServers.get(serverName);
        Objects.requireNonNull(s, "server is null (saraShowServers entry in config.yml is missing)");
        return PlayerWithServerMessage.read(s, in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull PlayerWithServerMessage msg) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User " + msg.getPlayer().getUniqueId() + " could not be found in the LuckPerms database.");
        }
        String username = user.getUsername();
        NodeMap map = user.getData(DataType.NORMAL);
        boolean modified = false;
        for (int saraGroup : Constants.SARA_GROUPS) {
            Node nodeSara = LuckPermsUtil.findParentNode(map, saraGroup + "yen", null);
            Node nodeHideSara = LuckPermsUtil.findParentNode(map, "hide" + saraGroup, null);
            if (nodeSara != null || nodeHideSara != null) {
                String actionDesc;
                Node nodeSaraShow = LuckPermsUtil.findParentNode(map, "show" + saraGroup + "yen", msg.getServer());
                if (nodeSaraShow != null) {
                    // hide
                    map.remove(nodeSaraShow);
                    actionDesc = "Removed show" + saraGroup + "yen from " + username + " in server=" + msg.getServer();
                    Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + saraGroup + "円皿を非表示にしました。"));
                } else {
                    // show
                    LuckPermsUtil.addGroup(map, "show" + saraGroup + "yen", msg.getServer(), -1);
                    actionDesc = "Added show" + saraGroup + "yen to " + username + " in server=" + msg.getServer();
                    Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + saraGroup + "円皿を表示しました。"));
                }
                modified = true;
                api.getActionLogger().submit(
                        Action.builder()
                                .targetType(Action.Target.Type.USER)
                                .timestamp(Instant.now())
                                .source(new UUID(0L, 0L))
                                .sourceName("AziPluginMessaging@" + api.getServerName())
                                .target(msg.getPlayer().getUniqueId())
                                .targetName(username)
                                .description(actionDesc)
                                .build());
            }
        }
        if (!modified) {
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7c権限がありません。皿を持ってるのにこのメッセージが出る場合はPerfectBoat#0001に泣きつきましょう！"));
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have any sara groups to toggle.");
        }
        api.getUserManager().saveUser(user);
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
    }
}
