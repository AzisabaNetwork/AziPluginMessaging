package net.azisaba.azipluginmessaging.api.protocol;

import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

public abstract class PacketQueue {
    public static final PacketQueue EMPTY = new Empty();

    public abstract void add(@NotNull Protocol<?, ?> protocol, @NotNull Message message);
    public abstract void flush(@NotNull PacketSender sender);

    private static class Empty extends PacketQueue {
        private Empty() {
        }

        @Override
        public void add(@NotNull Protocol<?, ?> protocol, @NotNull Message message) {
        }

        @Override
        public void flush(@NotNull PacketSender sender) {
        }
    }
}
