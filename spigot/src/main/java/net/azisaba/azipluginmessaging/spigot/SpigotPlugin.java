package net.azisaba.azipluginmessaging.spigot;

import net.azisaba.azipluginmessaging.api.AziPluginMessaging;
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

import java.security.KeyPair;
import java.util.Objects;

public class SpigotPlugin extends JavaPlugin implements Listener {
    public static SpigotPlugin plugin;

    @Override
    public void onLoad() {
        plugin = this;
        AziPluginMessaging api = new AziPluginMessagingSpigot(this);
        AziPluginMessagingProviderProvider.register(api);
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(Bukkit.getPluginCommand("azipluginmessaging")).setExecutor(new AziPluginMessagingCommand());
        try {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, Protocol.LEGACY_CHANNEL_ID);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, Protocol.LEGACY_CHANNEL_ID, new PluginMessageReceiver());
        } catch (IllegalArgumentException e) {
            getLogger().info("Could not register legacy channel, you can ignore this message if you're running on 1.13+");
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // do inside the separate thread to avoid blocking main thread
        new Thread(() -> {
            KeyPair keyPair;
            try {
                // generate keypair
                keyPair = EncryptionUtil.generateKeyPair(2048);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            PlayerImpl player = PlayerImpl.of(e.getPlayer());

            // set keypair
            player.setKeyPair(keyPair);

            // set encrypted flag to false if it was previously set to true
            player.setEncrypted(false);

            // set public key to null too
            player.setRemotePublicKeyInternal(null);

            player.challenge = TokenUtil.generateNewToken();

            // send our public key
            Protocol.P_ENCRYPTION.sendPacket(player, new EncryptionMessage(player.challenge, keyPair.getPublic()));
        }, "AziPluginMessaging-" + e.getPlayer().getName()).start();
    }
}
