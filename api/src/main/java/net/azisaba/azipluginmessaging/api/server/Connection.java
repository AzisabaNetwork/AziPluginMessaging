package net.azisaba.azipluginmessaging.api.server;

import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * Represents something that can have a connection.
 */
public interface Connection {
    /**
     * Marks the connection as encrypted.
     * @param encrypted true if the connection is encrypted; false otherwise
     */
    void setEncrypted(boolean encrypted);

    /**
     * Checks if the connection is encrypted.
     * @return true if the connection is encrypted; false otherwise
     */
    boolean isEncrypted();

    /**
     * Gets the local key pair for encrypting packets.
     * @return the local key pair
     */
    @NotNull
    KeyPair getKeyPair();

    /**
     * Gets the remote public key for decrypting packets.
     * @return the remote public key
     */
    @NotNull
    PublicKey getRemotePublicKey();
}
