package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundGiveSaraMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GiveSaraCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        int amount = Integer.parseInt(args[0]);
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(args[1]);
            boolean res = Protocol.P_GIVE_SARA.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), new ProxyboundGiveSaraMessage(amount, target));
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request to give " + target.getUsername() + " the " + amount + "yen sara");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet (attempted to give " + target.getUsernameOrUniqueId() + " the " + amount + "yen sara). Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "giveSara";
    }

    @Override
    public @NotNull String getDescription() {
        return "Give a player the sara rank";
    }

    @Override
    public @NotNull String getUsage() {
        return "<amount> <player>";
    }
}
