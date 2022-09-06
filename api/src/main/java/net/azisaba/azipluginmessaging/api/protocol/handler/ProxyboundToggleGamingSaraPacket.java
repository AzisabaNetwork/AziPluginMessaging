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
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ProxyboundToggleGamingSaraPacket implements ProxyMessageHandler<PlayerMessage> {
    @Override
    public @NotNull PlayerMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return PlayerMessage.read(in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull PlayerMessage msg) {
        toggle(sender, msg, "gamingsara");
    }

    public static void toggle(@NotNull PacketSender sender, @NotNull PlayerMessage msg, @NotNull String name) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User " + msg.getPlayer().getUniqueId() + " could not be found in the LuckPerms database.");
        }
        NodeMap map = user.getData(DataType.NORMAL);
        Node nodeChange = LuckPermsUtil.findParentNode(map, "change" + name, null);
        if (nodeChange == null) {
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7c権限がありません。皿を持ってるのにこのメッセージが出る場合は公式Discordのサポートまでお問い合わせください。"));
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have the group 'change" + name + "'.");
        }
        Node node = LuckPermsUtil.findParentNode(map, name, null);
        if (node != null) {
            map.remove(node);
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + name + "皿を非表示にしました。"));
        } else {
            LuckPermsUtil.addGroup(map, name, null, -1);
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + name + "皿を表示しました。"));
        }
        api.getUserManager().saveUser(user);
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
    }
}
