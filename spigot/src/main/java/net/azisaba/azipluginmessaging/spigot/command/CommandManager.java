package net.azisaba.azipluginmessaging.spigot.command;

import net.azisaba.azipluginmessaging.spigot.commands.*;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CommandManager {
    public static final List<Command> COMMANDS = Arrays.asList(
            new HelpCommand(),
            new SetRankCommand(),
            new GiveGamingSaraCommand(),
            new GiveSaraCommand(),
            new ToggleGamingSaraCommand(),
            new ToggleSaraHideCommand(),
            new ToggleSaraShowCommand(),
            new SetPrefixCommand(),
            new ClearPrefixCommand(),
            new GiveNitroSaraCommand(),
            new ToggleNitroSaraCommand(),
            new DumpProtocolCommand(),
            new PunishCommand()
    );

    public static @Nullable Command getCommand(String name) {
        for (Command command : COMMANDS) {
            if (command.getName().equalsIgnoreCase(name)) return command;
        }
        return null;
    }
}
