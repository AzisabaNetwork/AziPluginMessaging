package net.azisaba.azipluginmessaging.api.exception;

public class MissingPermissionException extends RuntimeException {
    public MissingPermissionException(String message) {
        super(message);
    }
}
