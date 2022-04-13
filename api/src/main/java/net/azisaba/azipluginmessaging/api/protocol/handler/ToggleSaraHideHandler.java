package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
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
import java.util.UUID;

public class ToggleSaraHideHandler implements MessageHandler<PlayerMessage> {
    @Override
    public @NotNull PlayerMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return PlayerMessage.read(in);
    }

    @Override
    public void handle(@NotNull PlayerMessage msg) {
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
            String desc = null;
            if (nodeSara != null) {
                map.remove(nodeSara);
                if (nodeHideSara == null) {
                    LuckPermsUtil.addGroup(map, "hide" + saraGroup, null, -1);
                }
                modified = true;
                desc = "Toggled " + saraGroup + "yen -> hide" + saraGroup + " for " + username;
            } else if (nodeHideSara != null) {
                map.remove(nodeHideSara);
                LuckPermsUtil.addGroup(map, saraGroup + "yen", null, -1);
                modified = true;
                desc = "Toggled hide" + saraGroup + " -> " + saraGroup + "yen for " + username;
            }
            if (desc != null) {
                api.getActionLogger().submit(
                        Action.builder()
                                .targetType(Action.Target.Type.USER)
                                .timestamp(Instant.now())
                                .source(new UUID(0L, 0L))
                                .sourceName("AziPluginMessaging@" + api.getServerName())
                                .target(msg.getPlayer().getUniqueId())
                                .targetName(username)
                                .description(desc)
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
