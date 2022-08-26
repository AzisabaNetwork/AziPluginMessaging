package net.azisaba.azipluginmessaging.api.protocol.message;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class EmptyMessage implements Message {
    public static final EmptyMessage INSTANCE = new EmptyMessage();

    private EmptyMessage() {}

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        // no-op
    }
}
