package net.azisaba.azipluginmessaging.api.util;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a new token.
     * @return the token
     */
    public static @NotNull String generateNewToken() {
        return new BigInteger(130, RANDOM).toString(32);
    }
}
