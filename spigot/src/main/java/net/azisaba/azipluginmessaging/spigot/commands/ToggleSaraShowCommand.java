package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundToggleSaraShowMessage;
import net.azisaba.azipluginmessaging.api.util.ArrayUtil;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ToggleSaraShowCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        Player target = PlayerUtil.getOfflinePlayer(args[0]);
        Set<Long> groups = Arrays.stream(ArrayUtil.dropFirst(args)).map(Long::parseLong).collect(Collectors.toSet());
        boolean res = Protocol.P_TOGGLE_SARA_SHOW.sendPacket(SpigotPlugin.getAnyPacketSender(), new ProxyboundToggleSaraShowMessage(target, groups));
        if (res) {
            sender.sendMessage(ChatColor.GREEN + "Sent a request to toggle " + target.getUsername() + "'s sara show flag.");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to send the packet. Maybe check console for errors?");
        }
    }

    @Override
    public @NotNull String getName() {
        return "toggleSaraShow";
    }

    @Override
    public @NotNull String getDescription() {
        return "Toggles whether to show the sara or not.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player>";
    }
}
