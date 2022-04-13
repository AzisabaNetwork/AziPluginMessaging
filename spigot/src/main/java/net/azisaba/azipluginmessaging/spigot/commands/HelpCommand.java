package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.command.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        for (Command command : CommandManager.COMMANDS) {
            sender.sendMessage(ChatColor.AQUA + ("/azipluginmessaging " + command.getName() + " " + command.getUsage()).trim() +
                    ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + command.getDescription());
        }
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public @NotNull String getDescription() {
        return "Displays the help message.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
