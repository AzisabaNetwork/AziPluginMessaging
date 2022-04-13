package net.azisaba.azipluginmessaging.spigot.command;

import net.azisaba.azipluginmessaging.spigot.commands.DumpProtocolCommand;
import net.azisaba.azipluginmessaging.spigot.commands.GiveGamingSaraCommand;
import net.azisaba.azipluginmessaging.spigot.commands.HelpCommand;
import net.azisaba.azipluginmessaging.spigot.commands.SetRankCommand;
import net.azisaba.azipluginmessaging.spigot.commands.ToggleGamingSaraCommand;
import net.azisaba.azipluginmessaging.spigot.commands.ToggleSaraHideCommand;
import net.azisaba.azipluginmessaging.spigot.commands.ToggleSaraShowCommand;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CommandManager {
    public static final List<Command> COMMANDS = Arrays.asList(
            new HelpCommand(),
            new SetRankCommand(),
            new GiveGamingSaraCommand(),
            new ToggleGamingSaraCommand(),
            new ToggleSaraHideCommand(),
            new ToggleSaraShowCommand(),
            new DumpProtocolCommand()
    );

    public static @Nullable Command getCommand(String name) {
        for (Command command : COMMANDS) {
            if (command.getName().equalsIgnoreCase(name)) return command;
        }
        return null;
    }
}
