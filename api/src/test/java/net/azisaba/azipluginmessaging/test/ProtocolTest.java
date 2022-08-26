package net.azisaba.azipluginmessaging.test;

import net.azisaba.azipluginmessaging.api.protocol.PacketFlow;
import net.azisaba.azipluginmessaging.api.protocol.Protocol;
import org.junit.jupiter.api.Test;

public class ProtocolTest {
    @Test
    public void load() {
        // check packets
        Protocol.getById(PacketFlow.TO_SERVER, (byte) 0);
    }
}
