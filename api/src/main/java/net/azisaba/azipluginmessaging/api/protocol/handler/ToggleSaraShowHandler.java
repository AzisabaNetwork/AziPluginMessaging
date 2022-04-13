package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerWithServerMessage;
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

public class ToggleSaraShowHandler implements MessageHandler<PlayerWithServerMessage> {
    @Override
    public @NotNull PlayerWithServerMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        String serverName = server.getServerInfo().getName();
        String s = AziPluginMessagingConfig.saraShowServers.get(serverName);
        Objects.requireNonNull(s, "server is null (saraShowServers entry in config.yml is missing)");
        return PlayerWithServerMessage.read(s, in);
    }

    @Override
    public void handle(@NotNull PlayerWithServerMessage msg) {
        Objects.requireNonNull(msg.getServer(), "server cannot be null");
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User " + msg.getPlayer().getUniqueId() + " could not be found in the LuckPerms database.");
        }
        String username = user.getUsername();
        NodeMap map = user.getData(DataType.NORMAL);
        boolean modified = false;
        for (int saraGroup : Constants.SARA_GROUPS) {
            Node nodeSara = LuckPermsUtil.findNode(map, saraGroup + "yen", null);
            Node nodeHideSara = LuckPermsUtil.findNode(map, "hide" + saraGroup, null);
            if (nodeSara != null || nodeHideSara != null) {
                String actionDesc;
                Node nodeSaraShow = LuckPermsUtil.findNode(map, "show" + saraGroup + "yen", msg.getServer());
                if (nodeSaraShow != null) {
                    map.remove(nodeSaraShow);
                    actionDesc = "Removed show" + saraGroup + "yen from " + username + " in server=" + msg.getServer();
                } else {
                    LuckPermsUtil.addGroup(map, "show" + saraGroup + "yen", msg.getServer(), -1);
                    actionDesc = "Added show" + saraGroup + "yen to " + username + " in server=" + msg.getServer();
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
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have any sara groups to toggle.");
        }
        api.getUserManager().saveUser(user);
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
    }
}
