package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundClearPrefixMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.LuckPermsUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class ProxyboundClearPrefixPacket implements ProxyMessageHandler<ProxyboundClearPrefixMessage> {
    @Override
    public @NotNull ProxyboundClearPrefixMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return ProxyboundClearPrefixMessage.read(server.getServerInfo().getName(), in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundClearPrefixMessage msg) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().loadUser(msg.getPlayer().getUniqueId()).join();
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("Could not find an user in LuckPerms database: " + msg.getPlayer().getUniqueId());
        }
        NodeMap map = user.data();
        if (msg.isGlobal()) {
            LuckPermsUtil.findPrefixNodes(map, null).forEach(map::remove);
        } else {
            LuckPermsUtil.findPrefixNodes(map, msg.getServer()).forEach(map::remove);
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
                        .description("Clear " + username + "'s prefix in server=" + msg.getServer())
                        .build());
    }
}
