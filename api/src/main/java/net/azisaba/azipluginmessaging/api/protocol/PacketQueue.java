package net.azisaba.azipluginmessaging.api.protocol;

import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

public abstract class PacketQueue {
    /**
     * Empty packet queue that does nothing.
     */
    public static final PacketQueue EMPTY = new Empty();

    /**
     * Adds the message to the queue.
     * @param protocol The protocol.
     * @param message The message.
     */
    public abstract void add(@NotNull Protocol<?, ?> protocol, @NotNull Message message);

    /**
     * Removes the message from the queue and sends all messages to provided sender.
     * @param sender the sender
     */
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
