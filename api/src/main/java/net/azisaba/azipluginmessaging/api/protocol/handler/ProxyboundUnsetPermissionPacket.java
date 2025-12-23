package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundNodeMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class ProxyboundUnsetPermissionPacket implements ProxyMessageHandler<ProxyboundNodeMessage> {
    public static final @NotNull String NODE_PREFIX = "azipluginmessaging.custom.";

    @Override
    public @NotNull ProxyboundNodeMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        String serverName = server.getServerInfo().getName();
        serverName = AziPluginMessagingConfig.servers.getOrDefault(serverName, serverName);
        return ProxyboundNodeMessage.read(serverName, in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundNodeMessage msg) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("Could not find an user in LuckPerms database: " + msg.getPlayer().getUniqueId());
        }
        NodeMap map = user.data();
        if (!map.remove(Node.builder(NODE_PREFIX + msg.getNode()).value(true).withContext("server", msg.getServer()).build()).wasSuccessful()) {
            throw new IllegalStateException("Failed to remove permission node");
        }
        String username = user.getUsername();
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
                        .description("permission unset " + NODE_PREFIX + msg.getNode() + " true server=" + msg.getServer())
                        .build());
    }
}
