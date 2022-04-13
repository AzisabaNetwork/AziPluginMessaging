package net.azisaba.azipluginmessaging.spigot.command;

import net.azisaba.azipluginmessaging.spigot.commands.HelpCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AziPluginMessagingCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            new HelpCommand().execute(sender, new String[0]);
            return true;
        }
        String commandName = args[0];
        try {
            net.azisaba.azipluginmessaging.spigot.command.Command cmd = CommandManager.getCommand(commandName);
            if (cmd == null) {
                sender.sendMessage(ChatColor.RED + "Unknown command: " + commandName);
                return true;
            }
            if (!sender.hasPermission("azipluginmessaging.command." + cmd.getName().toLowerCase(Locale.ROOT))) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            // drop first argument
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            cmd.execute(sender, newArgs);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An internal error occurred while executing command: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return Collections.emptyList();
        if (args.length == 1) return filter(CommandManager.COMMANDS.stream().map(net.azisaba.azipluginmessaging.spigot.command.Command::getName), args[0]);
        return Collections.emptyList();
    }

    private List<String> filter(Stream<String> stream, String prefix) {
        return stream.filter(s -> s.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
    }
}
