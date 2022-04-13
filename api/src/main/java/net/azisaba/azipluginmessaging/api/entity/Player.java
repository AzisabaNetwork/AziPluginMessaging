package net.azisaba.azipluginmessaging.api.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
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
     * Returns the username if present, and fallbacks to unique id.
     * @return username or unique id
     */
    @NotNull
    default String getUsernameOrUniqueId() {
        return getUsername() != null ? getUsername() : getUniqueId().toString();
    }
}
