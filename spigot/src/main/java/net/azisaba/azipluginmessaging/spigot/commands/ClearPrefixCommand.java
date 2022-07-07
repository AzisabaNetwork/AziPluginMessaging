package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundClearPrefixMessage;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.ArgumentParsedResult;
import xyz.acrylicstyle.util.ArgumentParser;
import xyz.acrylicstyle.util.ArgumentParserBuilder;
import xyz.acrylicstyle.util.InvalidArgumentException;

public class ClearPrefixCommand implements Command {
    private static final ArgumentParser PARSER =
            ArgumentParserBuilder.builder()
                    .literalBackslash()
                    .create();

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) throws InvalidArgumentException {
        ArgumentParsedResult result = PARSER.parse(String.join(" ", args));
        if (result.unhandledArguments().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(result.unhandledArguments().get(0));
            boolean global = result.containsShortArgument('g');
            boolean all = result.containsShortArgument('a');
            if (global && all) {
                sender.sendMessage(ChatColor.RED + "You can't use both -g and -a at the same time.");
                return;
            }
            boolean res = Protocol.P_CLEAR_PREFIX.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), new ProxyboundClearPrefixMessage(target, global, all));
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request to clear the prefix of " + target.getUsername() + "");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet. Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "clearPrefix";
    }

    @Override
    public @NotNull String getDescription() {
        return "Clear the prefix of a player.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player> [-g (global) | -a (all)]";
    }
}
