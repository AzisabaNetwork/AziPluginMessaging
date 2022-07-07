package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundGiveNitroSaraMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class GiveNitroSaraCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(args[0]);
            boolean res = Protocol.P_GIVE_NITRO_SARA.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), new ProxyboundGiveNitroSaraMessage(target, Integer.parseInt(args[1]), TimeUnit.MINUTES));
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request to give " + target.getUsername() + " the nitro sara");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet (attempted to give " + target.getUsernameOrUniqueId() + " the nitro sara). Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "giveNitroSara";
    }

    @Override
    public @NotNull String getDescription() {
        return "Give a player the nitro sara.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player> <time-in-minutes>";
    }
}
