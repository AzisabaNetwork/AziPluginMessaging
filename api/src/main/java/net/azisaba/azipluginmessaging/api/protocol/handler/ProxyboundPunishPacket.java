package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.EmptyMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundPunishMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

public class ProxyboundPunishPacket implements ProxyMessageHandler<ProxyboundPunishMessage> {
    @Override
    public @NotNull ProxyboundPunishMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        String serverName = server.getServerInfo().getName();
        serverName = AziPluginMessagingConfig.servers.getOrDefault(serverName, serverName);
        return ProxyboundPunishMessage.read(serverName, in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundPunishMessage msg) throws Exception {
        AziPluginMessagingProvider.get().getProxy().handle(msg);
    }
}
