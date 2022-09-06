package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.exception.MissingPermissionException;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundToggleSaraShowMessage;
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
import net.luckperms.api.track.Track;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

public class ProxyboundToggleSaraShowPacket implements ProxyMessageHandler<ProxyboundToggleSaraShowMessage> {
    @Override
    public @NotNull ProxyboundToggleSaraShowMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        String serverName = server.getServerInfo().getName();
        String s = AziPluginMessagingConfig.saraShowServers.get(serverName);
        Objects.requireNonNull(s, "server is null (saraShowServers entry in config.yml is missing)");
        return ProxyboundToggleSaraShowMessage.read(s, in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundToggleSaraShowMessage msg) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User " + msg.getPlayer().getUniqueId() + " could not be found in the LuckPerms database.");
        }
        NodeMap map = user.getData(DataType.NORMAL);
        boolean modified = false;
        Track track = api.getTrackManager().createAndLoadTrack("sara").join();
        for (String groupName : track.getGroups()) {
            long yen = Long.parseLong(groupName.replace("yen", ""));
            if (!msg.getGroups().isEmpty() && !msg.getGroups().contains(yen)) {
                continue;
            }
            Node nodeSara = LuckPermsUtil.findParentNode(map, groupName, null);
            Node nodeHideSara = LuckPermsUtil.findParentNode(map, "hide" + yen, null);
            if (nodeSara != null || nodeHideSara != null) {
                Node nodeSaraShow = LuckPermsUtil.findParentNode(map, "show" + yen + "yen", msg.getServer());
                if (nodeSaraShow != null) {
                    // hide
                    map.remove(nodeSaraShow);
                    Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + yen + "円皿を非表示にしました。"));
                } else {
                    // show
                    LuckPermsUtil.addGroup(map, "show" + yen + "yen", msg.getServer(), -1);
                    Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7a" + yen + "円皿を表示しました。"));
                }
                modified = true;
            }
        }
        if (!modified) {
            Protocol.S_ACTION_RESPONSE.sendPacket(sender, new ServerboundActionResponseMessage(msg.getPlayer().getUniqueId(), "\u00a7c権限がありません。皿を持ってるのにこのメッセージが出る場合は公式Discordのサポートまでお問い合わせください。"));
            throw new MissingPermissionException("User " + msg.getPlayer().getUniqueId() + " does not have any sara groups to toggle.");
        }
        api.getUserManager().saveUser(user);
        api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
    }
}
