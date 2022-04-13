package net.azisaba.azipluginmessaging.api.exception;

/**
 * Thrown by protocol handler when a player does not have a permission to use.
 */
public class MissingPermissionException extends RuntimeException {
    public MissingPermissionException(String message) {
        super(message);
    }
}
