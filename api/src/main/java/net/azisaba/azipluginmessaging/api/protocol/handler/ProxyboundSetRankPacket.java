package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.LuckPermsUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.Track;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ProxyboundSetRankPacket implements ProxyMessageHandler<ProxyboundSetRankMessage> {
    @Override
    public @NotNull ProxyboundSetRankMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        String serverName = server.getServerInfo().getName();
        String s = AziPluginMessagingConfig.rankableServers.get(serverName);
        Objects.requireNonNull(s, "server is null (rankableServers entry in config.yml is missing)");
        return ProxyboundSetRankMessage.read(s, in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundSetRankMessage msg) {
        Objects.requireNonNull(msg.getServer(), "server cannot be null");
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("Could not find an user in LuckPerms database: " + msg.getPlayer().getUniqueId());
        }
        String username = user.getUsername();
        Track track = api.getTrackManager().createAndLoadTrack("rank").join();
        if (!track.containsGroup(msg.getRank())) {
            throw new IllegalArgumentException("Group is not in a track: " + msg.getRank());
        }
        boolean modified = false;
        NodeMap nodes = user.getData(DataType.NORMAL);
        for (String group : track.getGroups()) {
            if (msg.getServer().equals(group)) continue;
            Node node = LuckPermsUtil.findNode(nodes, group, msg.getServer());
            if (node != null) {
                nodes.remove(node);
                modified = true;
            }
        }
        Node node = LuckPermsUtil.findNode(nodes, msg.getRank(), msg.getServer());
        if (node == null) {
            nodes.add(InheritanceNode.builder().group(msg.getRank()).context(ImmutableContextSet.of("server", msg.getServer())).build());
            modified = true;
        }
        if (modified) {
            api.getUserManager().saveUser(user);
            api.getMessagingService().ifPresent(service -> service.pushUserUpdate(user));
            api.getActionLogger().submit(
                    Action.builder()
                            .targetType(Action.Target.Type.USER)
                            .timestamp(Instant.now())
                            .source(new UUID(0L, 0L))
                            .sourceName("AziPluginMessaging[" + msg.getServer() + "]@" + api.getServerName())
                            .target(msg.getPlayer().getUniqueId())
                            .targetName(username)
                            .description("Set " + username + "'s rank to " + msg.getRank() + " in server=" + msg.getServer())
                            .build());
        }
    }
}
