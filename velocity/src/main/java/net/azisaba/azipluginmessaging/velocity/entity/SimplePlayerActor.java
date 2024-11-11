package net.azisaba.azipluginmessaging.velocity.entity;

import net.azisaba.spicyAzisaBan.SpicyAzisaBan;
import net.azisaba.spicyAzisaBan.common.Actor;
import net.azisaba.spicyAzisaBan.common.chat.Component;
import net.azisaba.spicyAzisaBan.struct.PlayerData;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record SimplePlayerActor(@NotNull String name, @NotNull UUID uuid) implements Actor {
    public SimplePlayerActor(@NotNull PlayerData playerData) {
        this(playerData.getName(), playerData.getUniqueId());
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        Component component2 = Component.text("behalf of [" + getName() + "] [" + getUniqueId() + "]: ");
        component2.addChildren(component);
        SpicyAzisaBan.instance.getConsoleActor().sendMessage(component2);
    }

    @Override
    public void sendMessage(@NotNull Component... components) {
        Component component = Component.text("behalf of [" + getName() + "] [" + getUniqueId() + "]: ");
        for (Component c : components) {
            component.addChildren(c);
        }
        SpicyAzisaBan.instance.getConsoleActor().sendMessage(component);
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return LuckPermsProvider.get()
                .getUserManager()
                .loadUser(uuid)
                .join()
                .getCachedData()
                .permissionData()
                .calculate(QueryOptions.defaultContextualOptions())
                .checkPermission(s) == Tristate.TRUE;
    }

    @Override
    public void sendMessage(@NotNull net.kyori.adventure.text.Component component) {
        SpicyAzisaBan.instance.getConsoleActor().sendMessage(
                net.kyori.adventure.text.Component.text("behalf of [" + getName() + "] [" + getUniqueId() + "]: ").append(component)
        );
    }
}
