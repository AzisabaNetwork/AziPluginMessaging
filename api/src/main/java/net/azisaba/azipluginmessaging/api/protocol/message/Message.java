package net.azisaba.azipluginmessaging.api.protocol.message;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Message {
    void write(@NotNull DataOutputStream out) throws IOException;
}
