package net.azisaba.azipluginmessaging.spigot.commands;

import net.azisaba.azipluginmessaging.api.protocol.PacketFlow;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.spigot.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DumpProtocolCommand implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        for (Protocol<?, ?> protocol : Protocol.values(PacketFlow.TO_PROXY)) {
            String hex = Integer.toString(protocol.getId(), 16);
            if (hex.length() == 1) hex = '0' + hex;
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "TO_PROXY " + ChatColor.GREEN + protocol.getId() +
                    " (0x" + hex + ") " +
                    ChatColor.WHITE + protocol.getHandler().getClass().getSimpleName());
        }
        for (Protocol<?, ?> protocol : Protocol.values(PacketFlow.TO_SERVER)) {
            String hex = Integer.toString(protocol.getId(), 16);
            if (hex.length() == 1) hex = '0' + hex;
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "TO_SERVER " + ChatColor.GREEN + protocol.getId() +
                    " (0x" + hex + ") " +
                    ChatColor.WHITE + protocol.getHandler().getClass().getSimpleName());
        }
    }

    @Override
    public @NotNull String getName() {
        return "dumpProtocol";
    }

    @Override
    public @NotNull String getDescription() {
        return "Shows all registered protocols.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
