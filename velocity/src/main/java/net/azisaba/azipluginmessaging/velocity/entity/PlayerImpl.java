package net.azisaba.azipluginmessaging.velocity.entity;

import net.azisaba.azipluginmessaging.api.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerImpl implements Player {
    private final com.velocitypowered.api.proxy.Player handle;

    public PlayerImpl(@NotNull com.velocitypowered.api.proxy.Player handle) {
        this.handle = handle;
    }

    @NotNull
    public com.velocitypowered.api.proxy.Player getHandle() {
        return handle;
    }

    @Override
    public @NotNull String getUsername() {
        return getHandle().getUsername();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return getHandle().getUniqueId();
    }
}
