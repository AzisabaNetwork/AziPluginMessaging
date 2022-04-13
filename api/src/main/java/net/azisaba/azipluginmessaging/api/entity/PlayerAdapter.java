package net.azisaba.azipluginmessaging.api.entity;

import org.jetbrains.annotations.NotNull;

public interface PlayerAdapter<T> {
    /**
     * Returns the player object from platform dependent player object.
     * @param player the platform dependent player
     * @return the player object
     */
    @NotNull
    Player get(@NotNull T player);
}
