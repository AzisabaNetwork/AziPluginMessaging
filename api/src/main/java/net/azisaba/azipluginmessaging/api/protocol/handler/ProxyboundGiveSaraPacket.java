package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundGiveSaraMessage;
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
import net.luckperms.api.track.Track;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class ProxyboundGiveSaraPacket implements ProxyMessageHandler<ProxyboundGiveSaraMessage> {
    @Override
    public @NotNull ProxyboundGiveSaraMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return new ProxyboundGiveSaraMessage(in.readInt(), SimplePlayer.read(in));
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundGiveSaraMessage msg) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User " + msg.getPlayer().getUniqueId() + " could not be found in the LuckPerms database.");
        }
        String groupName = msg.getAmount() + "yen";
        Track track = api.getTrackManager().createAndLoadTrack("sara").join();
        if (!track.containsGroup(groupName)) {
            throw new IllegalArgumentException("Group is not in a track: " + groupName);
        }
        String username = user.getUsername();
        boolean modified = false;
        NodeMap map = user.getData(DataType.NORMAL);
        Node nodeSara = LuckPermsUtil.findParentNode(map, groupName, null);
        if (nodeSara == null) {
            LuckPermsUtil.addGroup(map, groupName, null, -1);
            modified = true;
        }
        if (!modified) {
            Logger.getCurrentLogger().warn("Received GiveSara request for {} but they already have the rank", username);
            return;
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
                        .description("Added " + groupName + " sara to " + username)
                        .build());
    }
}
