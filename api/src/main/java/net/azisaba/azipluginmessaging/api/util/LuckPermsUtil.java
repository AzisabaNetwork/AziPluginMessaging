package net.azisaba.azipluginmessaging.api.util;

import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;

public class LuckPermsUtil {
    @Nullable
    public static Node findNode(@NotNull NodeMap map, @NotNull String group, @Nullable String server) {
        if (server == null) {
            return map.toCollection()
                    .stream()
                    .filter(node -> node.getType() == NodeType.INHERITANCE &&
                            Objects.equals(node.getKey(), "group." + group) &&
                            node.getValue() &&
                            !node.getContexts().getAnyValue("server").isPresent())
                    .findFirst()
                    .orElse(null);
        }
        return map.toCollection()
                .stream()
                .filter(node -> node.getType() == NodeType.INHERITANCE &&
                        Objects.equals(node.getKey(), "group." + group) &&
                        node.getValue() &&
                        node.getContexts().getValues("server").contains(server))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static DataMutateResult addGroup(
            @NotNull NodeMap map,
            @NotNull String group,
            @Nullable String server,
            @Range(from = -1, to = Long.MAX_VALUE) long expiryEpochSeconds) {
        InheritanceNode.Builder builder = InheritanceNode.builder(group).value(true);
        if (server != null) builder = builder.context(ImmutableContextSet.of("server", server));
        if (expiryEpochSeconds != -1) builder = builder.expiry(expiryEpochSeconds);
        return map.add(builder.build());
    }
}
