package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetPrefixMessage;
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

public class SetPrefixCommand implements Command {
    private static final ArgumentParser PARSER =
            ArgumentParserBuilder.builder()
                    .disallowEscapedLineTerminators()
                    .disallowEscapedTabCharacter()
                    .parseOptionsWithoutDash()
                    .create();

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) throws InvalidArgumentException {
        ArgumentParsedResult result = PARSER.parse(String.join(" ", args));
        if (result.unhandledArguments().size() < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(result.unhandledArguments().get(0));
            boolean global = result.containsShortArgument('g');
            String prefix = result.unhandledArguments().get(1).replace('\u00a7', '&');
            ProxyboundSetPrefixMessage message = ProxyboundSetPrefixMessage.createFromServerside(target, global, prefix);
            boolean res = Protocol.P_SET_PREFIX.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), message);
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request to set the prefix of " + target.getUsername() + " to " + prefix);
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet. Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "setPrefix";
    }

    @Override
    public @NotNull String getDescription() {
        return "Set the prefix of a player.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player> <prefix> [-g]";
    }
}
