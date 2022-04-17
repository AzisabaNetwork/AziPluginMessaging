package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GiveGamingSaraCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(args[0]);
            boolean res = Protocol.P_GIVE_GAMING_SARA.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), new PlayerMessage(target));
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request to give " + target.getUsername() + " the gaming sara");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet (attempted to give " + target.getUsernameOrUniqueId() + " the gaming sara). Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "giveGamingSara";
    }

    @Override
    public @NotNull String getDescription() {
        return "Give a player the gaming sara rank";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player>";
    }
}
