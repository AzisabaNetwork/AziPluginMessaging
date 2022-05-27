package net.azisaba.azipluginmessaging.api.protocol;

public enum PacketFlow {
    /**
     * Server (Backend) -> Proxy
     */
    TO_PROXY,
    /**
     * Proxy -> Server (Backend)
     */
    TO_SERVER,
}
