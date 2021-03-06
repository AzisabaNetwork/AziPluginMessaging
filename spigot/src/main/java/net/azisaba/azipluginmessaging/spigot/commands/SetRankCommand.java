package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetRankCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        Player target;
        String rank = args[0];
        if (args.length >= 2) {
            target = PlayerUtil.resolveNameOrUUID(args[1]);
        } else {
            if (!(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage(ChatColor.RED + "You are not a player");
                return;
            } else {
                target = PlayerImpl.of((org.bukkit.entity.Player) sender);
            }
        }
        boolean res = Protocol.P_SET_RANK.sendPacket(SpigotPlugin.getAnyPacketSender(), new ProxyboundSetRankMessage(rank, target));
        if (res) {
            sender.sendMessage(ChatColor.GREEN + "Sent a request to change " + target.getUsername() + "'s rank to " + rank);
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to send the packet (attempted to change " + target.getUsernameOrUniqueId() + "'s rank to " + rank + "). Maybe check console for errors?");
        }
    }

    @Override
    public @NotNull String getName() {
        return "setRank";
    }

    @Override
    public @NotNull String getDescription() {
        return "Sets the rank of the player";
    }

    @Override
    public @NotNull String getUsage() {
        return "<rank> [player]";
    }
}
