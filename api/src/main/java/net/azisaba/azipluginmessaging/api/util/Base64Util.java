package net.azisaba.azipluginmessaging.api.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public class Base64Util {
    /**
     * Encodes the given byte array to a base64 string.
     * @param bytes the byte array
     * @return the base64 string
     */
    @Contract("_ -> new")
    public static @NotNull String encode(byte @NotNull [] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decodes the given base64 string to a byte array.
     * @param string the base64 string
     * @return the byte array
     */
    public static byte @NotNull [] decode(@NotNull String string) {
        return Base64.getDecoder().decode(string);
    }
}
