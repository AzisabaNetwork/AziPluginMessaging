package net.azisaba.azipluginmessaging.api.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public interface Player {
    @Nullable
    String getUsername();

    @NotNull
    UUID getUniqueId();

    @NotNull
    default String getUsernameOrUniqueId() {
        return getUsername() != null ? getUsername() : getUniqueId().toString();
    }
}
