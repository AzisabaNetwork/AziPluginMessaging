package net.azisaba.azipluginmessaging.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProviderProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.velocity.server.ServerConnectionImpl;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Plugin(id = "azi-plugin-messaging", name = "AziPluginMessaging", version = "1.0.0",
        dependencies = @Dependency(id = "luckperms"))
public class VelocityPlugin {
    @Inject
    public VelocityPlugin(@NotNull ProxyServer server, @NotNull Logger logger) {
        server.getChannelRegistrar().register(new LegacyChannelIdentifier(Protocol.LEGACY_CHANNEL_ID));
        server.getChannelRegistrar().register(MinecraftChannelIdentifier.from(Protocol.CHANNEL_ID));
        AziPluginMessagingVelocity api = new AziPluginMessagingVelocity(server, logger);
        AziPluginMessagingProviderProvider.register(api);
        AziPluginMessagingConfig.reload();
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {
        //net.azisaba.azipluginmessaging.api.Logger.getCurrentLogger().info("Received plugin message: " + e.getIdentifier().getId() + " (source: " + e.getSource() + ")");
        if (!(e.getSource() instanceof ServerConnection) ||
                (!e.getIdentifier().getId().equals(Protocol.CHANNEL_ID) && !e.getIdentifier().getId().equals(Protocol.LEGACY_CHANNEL_ID))) {
            // hacking attempt or wrong channel
            return;
        }
        Protocol.handleProxySide(new ServerConnectionImpl((ServerConnection) e.getSource()), e.getData());
        e.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}
