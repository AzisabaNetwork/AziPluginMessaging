package net.azisaba.azipluginmessaging.spigot;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProviderProvider;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.EncryptionMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import net.azisaba.azipluginmessaging.api.util.TokenUtil;
import net.azisaba.azipluginmessaging.spigot.command.AziPluginMessagingCommand;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.KeyPair;
import java.util.Objects;

public class SpigotPlugin extends JavaPlugin implements Listener {
    public static SpigotPlugin plugin;
    private boolean processJoinEvent = false;

    @Override
    public void onLoad() {
        plugin = this;
        AziPluginMessaging api = new AziPluginMessagingSpigot(this);
        AziPluginMessagingProviderProvider.register(api);
        AziPluginMessagingConfig.reload();
    }

    @Override
    public void onEnable() {
        // Don't process PlayerJoinEvent for 20 ticks, the server may be lagging at this point.
        Bukkit.getScheduler().runTaskLater(this, () -> processJoinEvent = true, 20);

        Objects.requireNonNull(Bukkit.getPluginCommand("azipluginmessaging")).setExecutor(new AziPluginMessagingCommand());
        try {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, Protocol.LEGACY_CHANNEL_ID);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, Protocol.LEGACY_CHANNEL_ID, new PluginMessageReceiver());
        } catch (IllegalArgumentException e) {
            getLogger().info("Could not register legacy channel, you can ignore this message if you're running on 1.13+");
            if (AziPluginMessagingConfig.debug) {
                e.printStackTrace();
            }
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Protocol.CHANNEL_ID);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, Protocol.CHANNEL_ID, new PluginMessageReceiver());
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled " + getName());
    }

    @NotNull
    public static PacketSender getAnyPacketSender() {
        return AziPluginMessagingProvider.get().getServer().getPacketSender();
    }

    @Nullable
    public static PacketSender getAnyPacketSenderOrNull() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return null;
        }
        return ((AziPluginMessagingSpigot.ServerImpl) AziPluginMessagingProvider.get().getServer()).getPacketSenderOrNull();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!processJoinEvent) {
            // < 20 ticks
            return;
        }
        PlayerImpl player = PlayerImpl.of(e.getPlayer());
        player.setEncrypted(false);
        player.setRemotePublicKeyInternal(null);
        int i = player.joins.incrementAndGet();
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            if (player.joins.get() != i) {
                return;
            }

            KeyPair keyPair;
            try {
                // generate keypair
                keyPair = EncryptionUtil.generateKeyPair(2048);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // set keypair
            player.setKeyPair(keyPair);

            // set encrypted flag to false if it was previously set to true
            player.setEncrypted(false);

            // set public key to null too
            player.setRemotePublicKeyInternal(null);

            player.challenge = TokenUtil.generateNewToken();

            // send our public key
            Protocol.P_ENCRYPTION.sendPacket(player, new EncryptionMessage(player.challenge, keyPair.getPublic()));
        }, 20L);
    }
}
