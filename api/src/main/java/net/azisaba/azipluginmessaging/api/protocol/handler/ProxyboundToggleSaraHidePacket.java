package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
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
import java.util.UUID;

public class ProxyboundToggleSaraHidePacket implements ProxyMessageHandler<PlayerMessage> {
    @Override
    public @NotNull PlayerMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return PlayerMessage.read(in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull PlayerMessage msg) {
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
                // hide
                map.remove(nodeSara);
                if (nodeHideSara == null) {
                    LuckPermsUtil.addGroup(map, "hide" + saraGroup, null, -1);
                }
                modified = true;
                desc = "Toggled " + saraGroup + "yen -> hide" + saraGroup + " for " + username;
                Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + saraGroup + "円皿を非表示にしました。"));
            } else if (nodeHideSara != null) {
                // show
                map.remove(nodeHideSara);
                LuckPermsUtil.addGroup(map, saraGroup + "yen", null, -1);
                modified = true;
                desc = "Toggled hide" + saraGroup + " -> " + saraGroup + "yen for " + username;
                Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + saraGroup + "円皿を表示しました。"));
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
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7c権限がありません。皿を持ってるのにこのメッセージが出る場合はPerfectBoat#0001に泣きつきましょう！"));
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have any sara groups to toggle.");
        }
        api.getUserManager().saveUser(user);
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
    }
}
