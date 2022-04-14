package net.azisaba.azipluginmessaging.api.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public class Base64Util {
    @Contract("_ -> new")
    public static @NotNull String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decode(@NotNull String string) {
        return Base64.getDecoder().decode(string);
    }
}
