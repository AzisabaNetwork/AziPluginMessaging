package net.azisaba.azipluginmessaging.api.protocol.handler;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundCheckRankExpirationMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundCheckRankExpirationMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.ResultSet;

public class ProxyboundCheckRankExpirationPacket implements ProxyMessageHandler<ProxyboundCheckRankExpirationMessage> {
    @Override
    public @NotNull ProxyboundCheckRankExpirationMessage read(@NotNull ServerConnection server, @NotNull DataInputStream in) throws IOException {
        return ProxyboundCheckRankExpirationMessage.read(in);
    }

    @Override
    public void handle(@NotNull PacketSender sender, @NotNull ProxyboundCheckRankExpirationMessage msg) throws Exception {
        long expiresAt = AziPluginMessagingProvider.get().getProxy().getPreparedStatement("SELECT `expires_at` FROM `temp_rank` WHERE `player_uuid` = ? AND `rank` = ?", ps -> {
            ps.setString(1, msg.getPlayer().getUniqueId().toString());
            ps.setString(2, msg.getRank());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return -1L;
            }
            return rs.getLong("expires_at");
        }).join();
        ServerboundCheckRankExpirationMessage message = new ServerboundCheckRankExpirationMessage(msg.getPlayer(), msg.getRank(), expiresAt);
        Protocol.S_CHECK_RANK_EXPIRATION.sendPacket(sender, message);
    }
}
