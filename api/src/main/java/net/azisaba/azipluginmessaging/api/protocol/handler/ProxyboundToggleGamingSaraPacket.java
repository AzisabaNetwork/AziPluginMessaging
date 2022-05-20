package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundActionResponseMessage;
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
import java.time.Instant;
import java.util.UUID;

public class ProxyboundToggleGamingSaraPacket implements ProxyMessageHandler<PlayerMessage> {
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
        Node nodeChangeGamingSara = LuckPermsUtil.findParentNode(map, "changegamingsara", null);
        if (nodeChangeGamingSara == null) {
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7c権限がありません。皿を持ってるのにこのメッセージが出る場合はPerfectBoat#0001に泣きつきましょう！"));
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have the group 'changegamingsara'.");
        }
        char p;
        Node nodeGamingSara = LuckPermsUtil.findParentNode(map, "gamingsara", null);
        if (nodeGamingSara != null) {
            map.remove(nodeGamingSara);
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7aゲーミング皿を非表示にしました。"));
            p = '-';
        } else {
            LuckPermsUtil.addGroup(map, "gamingsara", null, -1);
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7aゲーミング皿を表示しました。"));
            p = '+';
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
