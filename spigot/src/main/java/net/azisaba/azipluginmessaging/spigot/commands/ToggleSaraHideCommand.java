package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundToggleSaraHideMessage;
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

public class ToggleSaraHideCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        Player target = PlayerUtil.getOfflinePlayer(args[0]);
        Set<Long> groups = Arrays.stream(ArrayUtil.dropFirst(args)).map(Long::parseLong).collect(Collectors.toSet());
        boolean res = Protocol.P_TOGGLE_SARA_HIDE.sendPacket(SpigotPlugin.getAnyPacketSender(), new ProxyboundToggleSaraHideMessage(target, groups));
        if (res) {
            sender.sendMessage(ChatColor.GREEN + "Sent a request to toggle " + target.getUsername() + "'s sara hide.");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to send the packet. Maybe check console for errors?");
        }
    }

    @Override
    public @NotNull String getName() {
        return "toggleSaraHide";
    }

    @Override
    public @NotNull String getDescription() {
        return "Toggles whether to hide the sara or not.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player>";
    }
}
