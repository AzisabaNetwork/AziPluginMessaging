package net.azisaba.azipluginmessaging.api.protocol;

import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.handler.MessageHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyMessageHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundEncryptionPacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundGiveGamingSaraPacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundGiveSaraPacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundSetRankPacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundToggleGamingSaraPacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundToggleSaraHidePacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ProxyboundToggleSaraShowPacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ServerMessageHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.ServerboundActionResponsePacket;
import net.azisaba.azipluginmessaging.api.protocol.handler.ServerboundEncryptionPacket;
import net.azisaba.azipluginmessaging.api.protocol.message.EncryptionMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerWithServerMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundGiveSaraMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ProxyboundSetRankMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.ServerboundActionResponseMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
import net.azisaba.azipluginmessaging.api.util.Constants;
import net.azisaba.azipluginmessaging.api.util.EncryptionUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Protocol<T extends MessageHandler<M>, M extends Message> {
    private static final Map<Byte, Protocol<?, ?>> TO_PROXY_BY_ID = new ConcurrentHashMap<>();
    private static final Map<Byte, Protocol<?, ?>> TO_SERVER_BY_ID = new ConcurrentHashMap<>();
    public static final String LEGACY_CHANNEL_ID = "AziPluginMessaging";
    public static final String CHANNEL_ID = "azipm:main";

    public static final Protocol<ProxyboundEncryptionPacket, EncryptionMessage> P_ENCRYPTION = new Protocol<>(PacketFlow.TO_PROXY, 0x00, new ProxyboundEncryptionPacket());
    public static final Protocol<ProxyboundSetRankPacket, ProxyboundSetRankMessage> P_SET_RANK = new Protocol<>(PacketFlow.TO_PROXY, 0x01, new ProxyboundSetRankPacket());
    public static final Protocol<ProxyboundGiveGamingSaraPacket, PlayerMessage> P_GIVE_GAMING_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x02, new ProxyboundGiveGamingSaraPacket());
    public static final Protocol<ProxyboundGiveSaraPacket, ProxyboundGiveSaraMessage> P_GIVE_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x03, new ProxyboundGiveSaraPacket());
    public static final Protocol<ProxyboundToggleGamingSaraPacket, PlayerMessage> P_TOGGLE_GAMING_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x04, new ProxyboundToggleGamingSaraPacket());
    public static final Protocol<ProxyboundToggleSaraHidePacket, PlayerMessage> P_TOGGLE_SARA_HIDE = new Protocol<>(PacketFlow.TO_PROXY, 0x05, new ProxyboundToggleSaraHidePacket()); // Note that this is non-contextual
    public static final Protocol<ProxyboundToggleSaraShowPacket, PlayerWithServerMessage> P_TOGGLE_SARA_SHOW = new Protocol<>(PacketFlow.TO_PROXY, 0x06, new ProxyboundToggleSaraShowPacket()); // Note that this is contextual

    public static final Protocol<ServerboundEncryptionPacket, EncryptionMessage> S_ENCRYPTION = new Protocol<>(PacketFlow.TO_SERVER, 0x00, new ServerboundEncryptionPacket());
    public static final Protocol<ServerboundActionResponsePacket, ServerboundActionResponseMessage> S_ACTION_RESPONSE = new Protocol<>(PacketFlow.TO_SERVER, 0x01, new ServerboundActionResponsePacket());

    private final PacketFlow packetFlow;
    private final byte id;
    private final T handler;

    private Protocol(@NotNull PacketFlow packetFlow, int id, @NotNull T handler) {
        this.packetFlow = packetFlow;
        this.id = (byte) (id & 0xFF);
        this.handler = handler;
        if (packetFlow == PacketFlow.TO_PROXY) {
            if (!(handler instanceof ProxyMessageHandler)) {
                throw new IllegalArgumentException("Handler must be instance of ProxyMessageHandler");
            }
            if (TO_PROXY_BY_ID.containsKey(this.id)) {
                throw new AssertionError("Duplicate protocol id: " + this.id);
            }
            TO_PROXY_BY_ID.put(this.id, this);
        } else {
            if (!(handler instanceof ServerMessageHandler)) {
                throw new IllegalArgumentException("Handler must be instance of ServerMessageHandler");
            }
            if (TO_SERVER_BY_ID.containsKey(this.id)) {
                throw new AssertionError("Duplicate protocol id: " + this.id);
            }
            TO_SERVER_BY_ID.put(this.id, this);
        }
    }

    /**
     * Returns the packet id.
     * @return id
     */
    public byte getId() {
        return id;
    }

    /**
     * Returns the handler registered when this protocol was created.
     * @return the handler
     */
    @NotNull
    public T getHandler() {
        return handler;
    }

    /**
     * Attempt to send a packet.
     * @param sender the packet sender to send the packet from.
     * @param msg the message to send
     * @return true if the message was sent successfully, false otherwise.
     */
    public boolean sendPacket(@NotNull PacketSender sender, @NotNull M msg) {
        if (id != 0 && !sender.isEncrypted()) {
            throw new IllegalStateException("Cannot send packet " + id + " without encryption");
        }
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(bout)) {
            out.writeByte(id);
            msg.write(out);
            byte[] bytes = bout.toByteArray();
            if (sender.isEncrypted()) {
                try {
                    bytes = EncryptionUtil.encrypt(bytes, sender.getRemotePublicKey());
                } catch (Exception e) {
                    throw new RuntimeException("Could not encrypt the packet (sender: " + sender + ")", e);
                }
            }
            if (Constants.DEBUG) {
                String hex = Integer.toString(id, 16);
                if (hex.length() == 1) hex = '0' + hex;
                Logger.getCurrentLogger().info("Sending packet {} (0x{}) to {} (encrypted: {})", id, hex, sender, sender.isEncrypted());
            }
            return sender.sendPacket(bytes);
        } catch (IOException e) {
            Logger.getCurrentLogger().warn("Failed to send packet", e);
            return false;
        }
    }

    /**
     * This method is called when a packet is received (proxy-side).
     * @param server the server connection
     * @param rawData the data of the packet
     */
    public static void handleProxySide(ServerConnection server, byte[] rawData) {
        byte[] data;
        if (server.isEncrypted()) {
            try {
                data = EncryptionUtil.decrypt(rawData, server.getKeyPair().getPrivate());
            } catch (Exception e) {
                throw new RuntimeException("Could not decrypt the packet (server: " + server + ")", e);
            }
        } else {
            data = rawData;
        }
        try (ByteArrayInputStream bin = new ByteArrayInputStream(data);
             DataInputStream in = new DataInputStream(bin)) {
            byte id = (byte) (in.readByte() & 0xFF);
            if (id != 0 && !server.isEncrypted()) {
                throw new RuntimeException("Packet " + id + " must be sent encrypted (server: " + server + ")");
            }
            Protocol<?, ?> protocol = Protocol.getById(PacketFlow.TO_PROXY, id);
            if (protocol == null) {
                Logger.getCurrentLogger().warn(
                        "Received unknown protocol id from server connection {}: {}",
                        server, id);
                return;
            }
            if (Constants.DEBUG) {
                String hex = Integer.toString(id, 16);
                if (hex.length() == 1) hex = '0' + hex;
                Logger.getCurrentLogger().info("Received packet {} (0x{}) from server connection {} (encrypted: {})", id, hex, server, server.isEncrypted());
            }
            if (protocol.packetFlow != PacketFlow.TO_PROXY) {
                throw new IllegalArgumentException("Packet " + protocol + " is not proxybound");
            }
            @SuppressWarnings("unchecked")
            ProxyMessageHandler<Message> handler = (ProxyMessageHandler<Message>) protocol.getHandler();
            Message message = handler.read(server, in);
            handler.handle(server, message);
        } catch (Exception | AssertionError e) {
            Logger.getCurrentLogger().warn("Failed to handle plugin message from " + server, e);
        }
    }

    /**
     * This method is called when a packet is received (server-side).
     * @param rawData the data of the packet
     */
    public static void handleServerSide(@NotNull PacketSender sender, byte[] rawData) {
        byte[] data;
        if (sender.isEncrypted()) {
            try {
                data = EncryptionUtil.decrypt(rawData, sender.getKeyPair().getPrivate());
            } catch (Exception e) {
                throw new RuntimeException("Could not decrypt the packet (sender: " + sender + ")", e);
            }
        } else {
            data = rawData;
        }
        try (ByteArrayInputStream bin = new ByteArrayInputStream(data);
             DataInputStream in = new DataInputStream(bin)) {
            byte id = (byte) (in.readByte() & 0xFF);
            if (id != 0 && !sender.isEncrypted()) {
                throw new RuntimeException("Packet " + id + " must be sent encrypted (sender: " + sender + ")");
            }
            Protocol<?, ?> protocol = Protocol.getById(PacketFlow.TO_SERVER, id);
            if (protocol == null) {
                Logger.getCurrentLogger().warn("Received unknown protocol id from {}: {}", sender, id);
                return;
            }
            if (Constants.DEBUG) {
                String hex = Integer.toString(id, 16);
                if (hex.length() == 1) hex = '0' + hex;
                Logger.getCurrentLogger().info("Received packet {} (0x{}) from {}", id, hex, sender);
            }
            if (protocol.packetFlow != PacketFlow.TO_SERVER) {
                throw new IllegalArgumentException("Packet " + protocol + " is not serverbound");
            }
            @SuppressWarnings("unchecked")
            ServerMessageHandler<Message> handler = (ServerMessageHandler<Message>) protocol.getHandler();
            Message message = handler.read(in);
            handler.handle(sender, message);
        } catch (Exception | AssertionError e) {
            Logger.getCurrentLogger().warn("Failed to handle plugin message from " + sender, e);
        }
    }

    @NotNull
    @Contract(pure = true)
    public PacketFlow getPacketFlow() {
        return packetFlow;
    }

    /**
     * Gets the protocol (packet) by its id.
     * @param id the id
     * @return the protocol, or null if not found
     */
    @Nullable
    public static Protocol<?, ?> getById(@NotNull PacketFlow packetFlow, byte id) {
        if (packetFlow == PacketFlow.TO_PROXY) {
            return TO_PROXY_BY_ID.get(id);
        } else {
            return TO_SERVER_BY_ID.get(id);
        }
    }

    /**
     * Returns all the protocols.
     * @return list of protocols
     */
    @Contract(pure = true)
    @NotNull
    public static Collection<Protocol<?, ?>> values(@NotNull PacketFlow packetFlow) {
        if (packetFlow == PacketFlow.TO_PROXY) {
            return TO_PROXY_BY_ID.values();
        } else {
            return TO_SERVER_BY_ID.values();
        }
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "id=" + id +
                ", handler=" + handler +
                '}';
    }
}
