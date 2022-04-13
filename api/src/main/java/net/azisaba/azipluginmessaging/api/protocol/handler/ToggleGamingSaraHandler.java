package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
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
import java.time.Instant;
import java.util.UUID;

public class ToggleGamingSaraHandler implements MessageHandler<PlayerMessage> {
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
        Node nodeChangeGamingSara = LuckPermsUtil.findNode(map, "changegamingsara", null);
        if (nodeChangeGamingSara == null) {
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have the group 'changegamingsara'.");
        }
        char p = '+';
        Node nodeGamingSara = LuckPermsUtil.findNode(map, "gamingsara", null);
        if (nodeGamingSara != null) {
            map.remove(nodeGamingSara);
            p = '-';
        } else {
            LuckPermsUtil.addGroup(map, "gamingsara", null, -1);
        }
        api.getUserManager().saveUser(user);
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
        api.getActionLogger().submit(
                Action.builder()
                        .targetType(Action.Target.Type.USER)
                        .timestamp(Instant.now())
                        .source(new UUID(0L, 0L))
                        .sourceName("AziPluginMessaging@" + api.getServerName())
                        .target(msg.getPlayer().getUniqueId())
                        .targetName(username)
                        .description("Toggled " + p + "gamingsara for " + username)
                        .build());
    }
}
