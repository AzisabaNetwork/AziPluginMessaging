package net.azisaba.azipluginmessaging.api;

import org.jetbrains.annotations.NotNull;

public class AziPluginMessagingProviderProvider {
    public static void register(@NotNull AziPluginMessaging api) {
        AziPluginMessagingProvider.register(api);
    }
}
