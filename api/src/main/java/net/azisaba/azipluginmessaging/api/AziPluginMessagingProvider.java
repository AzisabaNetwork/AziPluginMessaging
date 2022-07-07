package net.azisaba.azipluginmessaging.api;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Provider for {@link AziPluginMessaging}. Use {@link #get()} to obtain the instance of {@link AziPluginMessaging}.
 */
public final class AziPluginMessagingProvider {
    private static final String NOT_LOADED_MESSAGE = "AziPluginMessaging is not loaded yet!\n" +
            "Possible reasons:\n" +
            "  - the AziPluginMessaging plugin is not installed or threw exception while initializing\n" +
            "  - the plugin is not in the dependency of the plugin in the stacktrace\n" +
            "  - tried to access the API before the plugin is loaded (such as constructor)\n" +
            "    Call #get() in the plugin's onEnable() method (or equivalent one) to load the API correctly!";

    private static AziPluginMessaging api;

    private AziPluginMessagingProvider() {
        throw new AssertionError();
    }

    /**
     * Returns the instance of {@link AziPluginMessaging}.
     * @return {@link AziPluginMessaging}
     * @throws IllegalStateException if the API is not loaded yet
     */
    @NotNull
    public static AziPluginMessaging get() throws IllegalStateException {
        AziPluginMessaging api = AziPluginMessagingProvider.api;
        if (api == null) {
            throw new IllegalStateException(NOT_LOADED_MESSAGE);
        }
        return api;
    }

    @Internal
    static void register(@NotNull AziPluginMessaging api) {
        if (AziPluginMessagingProvider.api != null) {
            throw new IllegalStateException("API singleton already initialized");
        }
        Objects.requireNonNull(api);
        AziPluginMessagingProvider.api = api;
    }
}
