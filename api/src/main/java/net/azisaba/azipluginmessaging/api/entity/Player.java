package net.azisaba.azipluginmessaging.api.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Player {
    /**
     * Returns the player's username.
     * @return the username
     */
    @Nullable
    String getUsername();

    /**
     * Returns the player's unique id.
     * @return the unique id
     */
    @NotNull
    UUID getUniqueId();

    /**
     * Sends a message to the player.
     * @param message the message
     */
    void sendMessage(@NotNull String message);

    /**
     * Returns the username if present, and fallbacks to unique id.
     * @return username or unique id
     */
    @NotNull
    default String getUsernameOrUniqueId() {
        return getUsername() != null ? getUsername() : getUniqueId().toString();
    }

    /**
     * Checks if the player has the challenge equals to the provided challenge. Challenge token is used for securing
     * initial encryption packet.
     * @param challenge the challenge token to check
     * @return true if the player has the challenge equals to the provided challenge
     */
    boolean isChallengeEquals(@NotNull String challenge);
}
