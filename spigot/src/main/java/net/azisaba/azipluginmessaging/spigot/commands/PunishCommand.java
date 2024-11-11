package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.entity.Player;
import net.azisaba.azipluginmessaging.api.entity.SimplePlayer;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundPunishMessage;
import net.azisaba.azipluginmessaging.api.punishment.PunishmentType;
import net.azisaba.azipluginmessaging.spigot.SpigotPlugin;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import net.azisaba.azipluginmessaging.spigot.entity.PlayerImpl;
import net.azisaba.azipluginmessaging.spigot.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.ArgumentParsedResult;
import xyz.acrylicstyle.util.ArgumentParser;
import xyz.acrylicstyle.util.ArgumentParserBuilder;
import xyz.acrylicstyle.util.InvalidArgumentException;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PunishCommand implements Command {
    private static final ArgumentParser PARSER =
            ArgumentParserBuilder.builder()
                    .disallowEscapedLineTerminators()
                    .disallowEscapedTabCharacter()
                    .parseOptionsWithoutDash()
                    .create();

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) throws InvalidArgumentException {
        ArgumentParsedResult result = PARSER.parse(String.join(" ", args));
        if (result.unhandledArguments().size() < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getFullUsage());
            return;
        }
        new Thread(() -> {
            Player target = PlayerUtil.getOfflinePlayer(result.unhandledArguments().get(0));
            Player senderPlayer =
                    sender instanceof org.bukkit.entity.Player
                            ? PlayerImpl.of((org.bukkit.entity.Player) sender)
                            : new SimplePlayer(new UUID(0, 0), "CONSOLE");
            PunishmentType type = PunishmentType.valueOf(result.unhandledArguments().get(1).toUpperCase(Locale.ROOT));
            String reason = result.unhandledArguments().get(2);
            int time = result.unhandledArguments().size() == 3 ? 0 : Integer.parseInt(result.unhandledArguments().get(3));
            TimeUnit unit = result.unhandledArguments().size() == 3 ? null : TimeUnit.valueOf(result.unhandledArguments().get(4).toUpperCase(Locale.ROOT));
            ProxyboundPunishMessage message = new ProxyboundPunishMessage(
                    "",
                    target,
                    senderPlayer,
                    type,
                    reason,
                    time,
                    unit
            );
            boolean res = Protocol.P_PUNISH.sendPacket(SpigotPlugin.getAnyPacketSenderOrNull(), message);
            if (res) {
                sender.sendMessage(ChatColor.GREEN + "Sent a request!");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to send the packet. Maybe check console for errors?");
            }
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "punish";
    }

    @Override
    public @NotNull String getDescription() {
        return "Punish a player.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<player> <type> <reason> [time] [unit]";
    }
}
