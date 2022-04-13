package net.azisaba.azipluginmessaging.api.protocol.message;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents the base class of all (decoded) messages.
 */
public interface Message {
    /**
     * Writes the message to the specified {@link DataOutputStream}.
     * @param out The {@link DataOutputStream} to write to.
     * @throws IOException If an I/O error occurs.
     */
    void write(@NotNull DataOutputStream out) throws IOException;
}
