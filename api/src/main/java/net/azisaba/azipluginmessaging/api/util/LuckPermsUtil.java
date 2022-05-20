package net.azisaba.azipluginmessaging.api.util;

import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.stream.Stream;

public class LuckPermsUtil {
    /**
     * Finds the inheritance node from the given node map.
     * @param map the node map
     * @param group the group to find
     * @param server the server to apply server context to
     * @return the node, or null if not found
     */
    @Nullable
    public static Node findParentNode(@NotNull NodeMap map, @NotNull String group, @Nullable String server) {
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

    /**
     * Adds the given group to the given node map.
     * @param map the node map
     * @param group the group to add
     * @param server the server to apply server context to
     * @param expiryEpochSeconds the expiry epoch seconds; -1 to never expire
     * @return the result of the operation
     */
    @SuppressWarnings("UnusedReturnValue")
    public static @NotNull DataMutateResult addGroup(
            @NotNull NodeMap map,
            @NotNull String group,
            @Nullable String server,
            @Range(from = -1, to = Long.MAX_VALUE) long expiryEpochSeconds) {
        InheritanceNode.Builder builder = InheritanceNode.builder(group).value(true);
        if (server != null) builder = builder.withContext("server", server);
        if (expiryEpochSeconds != -1) builder = builder.expiry(expiryEpochSeconds);
        return map.add(builder.build());
    }

    @NotNull
    public static Stream<Node> findPrefixNodes(@NotNull NodeMap map, @Nullable String server) {
        if (server == null) {
            return map.toCollection()
                    .stream()
                    .filter(node -> node.getType() == NodeType.PREFIX &&
                            node.getKey().startsWith("prefix.666.") &&
                            node.getValue() &&
                            !node.getContexts().getAnyValue("server").isPresent());
        }
        return map.toCollection()
                .stream()
                .filter(node -> node.getType() == NodeType.PREFIX &&
                        node.getKey().startsWith("prefix.666.") &&
                        node.getValue() &&
                        node.getContexts().getValues("server").contains(server));
    }

    @Nullable
    public static Node findPrefixNode(@NotNull NodeMap map, @NotNull String prefix, @Nullable String server) {
        if (server == null) {
            return map.toCollection()
                    .stream()
                    .filter(node -> node.getType() == NodeType.PREFIX &&
                            Objects.equals(node.getKey(), "prefix.666." + prefix) &&
                            node.getValue() &&
                            !node.getContexts().getAnyValue("server").isPresent())
                    .findFirst()
                    .orElse(null);
        }
        return map.toCollection()
                .stream()
                .filter(node -> node.getType() == NodeType.PREFIX &&
                        Objects.equals(node.getKey(), "prefix.666." + prefix) &&
                        node.getValue() &&
                        node.getContexts().getValues("server").contains(server))
                .findFirst()
                .orElse(null);
    }

    public static @NotNull DataMutateResult setPrefix(@NotNull NodeMap map, @NotNull String prefix, @Nullable String server) {
        PrefixNode.Builder builder = PrefixNode.builder(prefix, 666).value(true);
        if (server != null) builder = builder.withContext("server", server);
        return map.add(builder.build());
    }
}
