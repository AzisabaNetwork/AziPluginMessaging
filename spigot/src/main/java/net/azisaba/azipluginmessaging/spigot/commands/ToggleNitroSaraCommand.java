package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToggleNitroSaraCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        Player target;
        if (args.length >= 1) {
            target = PlayerUtil.resolveNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
                return;
            } else {
                target = PlayerImpl.of((org.bukkit.entity.Player) sender);
            }
        }
        boolean res = Protocol.P_TOGGLE_NITRO_SARA.sendPacket(SpigotPlugin.getAnyPacketSender(), new PlayerMessage(target));
        if (res) {
            sender.sendMessage(ChatColor.GREEN + "Sent a request to toggle " + target.getUsername() + "'s nitro sara");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to send the packet. Maybe check console for errors?");
        }
    }

    @Override
    public @NotNull String getName() {
        return "toggleNitroSara";
    }

    @Override
    public @NotNull String getDescription() {
        return "Toggles the nitro sara";
    }

    @Override
    public @NotNull String getUsage() {
        return "[player]";
    }
}
