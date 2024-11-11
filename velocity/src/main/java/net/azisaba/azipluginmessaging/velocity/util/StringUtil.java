package net.azisaba.azipluginmessaging.velocity.util;

import org.jetbrains.annotations.NotNull;

public class StringUtil {
    public static @NotNull String escapeQuotes(@NotNull String str) {
        return str.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
