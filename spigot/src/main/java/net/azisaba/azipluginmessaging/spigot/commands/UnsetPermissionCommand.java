package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundSetPermissionPacket;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundNodeMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnsetPermissionCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(args[0]);
            boolean res = Protocol.P_UNSET_PERMISSION.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), new ProxyboundNodeMessage(target, args[1]));
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request to unset permission '" + ProxyboundSetPermissionPacket.NODE_PREFIX + args[1] + "' for a player " + target.getUsername());
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet (attempted to unset permission for a player " + target.getUsernameOrUniqueId() + "). Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "unsetPermission";
    }

    @Override
    public @NotNull String getDescription() {
        return "Unset custom permission for a player";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player> <node>";
    }
}
