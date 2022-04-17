package net.azisaba.azipluginmessaging.spigot.protocol;

import net.azisaba.azipluginmessaging.api.protocol.PacketQueue;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SimplePacketQueue extends PacketQueue {
    private final List<Map.Entry<Protocol<?, ?>, Message>> list = new ArrayList<>();

    public void add(@NotNull Protocol<?, ?> protocol, @NotNull Message message) {
        Objects.requireNonNull(protocol, "protocol must not be null");
        Objects.requireNonNull(message, "message must not be null");
        list.add(new AbstractMap.SimpleImmutableEntry<>(protocol, message));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void flush(@NotNull PacketSender sender) {
        for (Map.Entry<Protocol<?, ? extends Message>, ? extends Message> entry : list) {
            ((Protocol) entry.getKey()).sendPacket(sender, entry.getValue());
        }
        list.clear();
    }
}
