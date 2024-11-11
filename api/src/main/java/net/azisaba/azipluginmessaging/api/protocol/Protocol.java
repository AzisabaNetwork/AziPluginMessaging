package net.azisaba.azipluginmessaging.api.protocol;

import net.azisaba.azipluginmessaging.api.AziPluginMessagingConfig;
import net.azisaba.azipluginmessaging.api.AziPluginMessagingProvider;
import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.handler.*;
import net.azisaba.azipluginmessaging.api.protocol.message.*;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Protocol<T extends MessageHandler<M>, M extends Message> {
    private static final Map<Byte, Protocol<?, ?>> TO_PROXY_BY_ID = new ConcurrentHashMap<>();
    private static final Map<Byte, Protocol<?, ?>> TO_SERVER_BY_ID = new ConcurrentHashMap<>();

    /**
     * Legacy plugin channel id
     */
    public static final String LEGACY_CHANNEL_ID = "AziPluginMessaging";

    /**
     * Modern plugin channel id
     */
    public static final String CHANNEL_ID = "azipm:main";

    public static final Protocol<ProxyboundEncryptionPacket, EncryptionMessage> P_ENCRYPTION = new Protocol<>(PacketFlow.TO_PROXY, 0x00, new ProxyboundEncryptionPacket());
    public static final Protocol<ProxyboundEncryptionResponsePacket, EmptyMessage> P_ENCRYPTION_RESPONSE = new Protocol<>(PacketFlow.TO_PROXY, 0x01, new ProxyboundEncryptionResponsePacket());
    public static final Protocol<ProxyboundSetRankPacket, ProxyboundSetRankMessage> P_SET_RANK = new Protocol<>(PacketFlow.TO_PROXY, 0x02, new ProxyboundSetRankPacket());
    public static final Protocol<ProxyboundGiveGamingSaraPacket, PlayerMessage> P_GIVE_GAMING_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x03, new ProxyboundGiveGamingSaraPacket());
    public static final Protocol<ProxyboundGiveSaraPacket, ProxyboundGiveSaraMessage> P_GIVE_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x04, new ProxyboundGiveSaraPacket());
    public static final Protocol<ProxyboundToggleGamingSaraPacket, PlayerMessage> P_TOGGLE_GAMING_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x05, new ProxyboundToggleGamingSaraPacket());
    public static final Protocol<ProxyboundToggleSaraHidePacket, ProxyboundToggleSaraHideMessage> P_TOGGLE_SARA_HIDE = new Protocol<>(PacketFlow.TO_PROXY, 0x06, new ProxyboundToggleSaraHidePacket()); // non-contextual
    public static final Protocol<ProxyboundToggleSaraShowPacket, ProxyboundToggleSaraShowMessage> P_TOGGLE_SARA_SHOW = new Protocol<>(PacketFlow.TO_PROXY, 0x07, new ProxyboundToggleSaraShowPacket()); // contextual
    public static final Protocol<ProxyboundSetPrefixPacket, ProxyboundSetPrefixMessage> P_SET_PREFIX = new Protocol<>(PacketFlow.TO_PROXY, 0x08, new ProxyboundSetPrefixPacket()); // may be contextual
    public static final Protocol<ProxyboundClearPrefixPacket, ProxyboundClearPrefixMessage> P_CLEAR_PREFIX = new Protocol<>(PacketFlow.TO_PROXY, 0x09, new ProxyboundClearPrefixPacket()); // may be contextual
    public static final Protocol<ProxyboundGiveNitroSaraPacket, ProxyboundGiveNitroSaraMessage> P_GIVE_NITRO_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x0A, new ProxyboundGiveNitroSaraPacket());
    public static final Protocol<ProxyboundToggleNitroSaraPacket, PlayerMessage> P_TOGGLE_NITRO_SARA = new Protocol<>(PacketFlow.TO_PROXY, 0x0B, new ProxyboundToggleNitroSaraPacket());
    public static final Protocol<ProxyboundCheckRankExpirationPacket, ProxyboundCheckRankExpirationMessage> P_CHECK_RANK_EXPIRATION = new Protocol<>(PacketFlow.TO_PROXY, 0x0C, new ProxyboundCheckRankExpirationPacket());
    public static final Protocol<ProxyboundPunishPacket, ProxyboundPunishMessage> P_PUNISH = new Protocol<>(PacketFlow.TO_PROXY, 0x0D, new ProxyboundPunishPacket());

    public static final Protocol<ServerboundEncryptionPacket, EncryptionMessage> S_ENCRYPTION = new Protocol<>(PacketFlow.TO_SERVER, 0x00, new ServerboundEncryptionPacket());
    public static final Protocol<ServerboundEncryptionResponsePacket, EmptyMessage> S_ENCRYPTION_RESPONSE = new Protocol<>(PacketFlow.TO_SERVER, 0x01, new ServerboundEncryptionResponsePacket());
    public static final Protocol<ServerboundActionResponsePacket, ServerboundActionResponseMessage> S_ACTION_RESPONSE = new Protocol<>(PacketFlow.TO_SERVER, 0x02, new ServerboundActionResponsePacket());
    public static final Protocol<ServerboundCheckRankExpirationPacket, ServerboundCheckRankExpirationMessage> S_CHECK_RANK_EXPIRATION = new Protocol<>(PacketFlow.TO_SERVER, 0x03, new ServerboundCheckRankExpirationPacket());

    private final PacketFlow packetFlow;
    private final byte id;
    private final T handler;

    /**
     * Creates a new packet. Handler should be a subclass of {@link ProxyMessageHandler} if packet flow is
     * {@link PacketFlow#TO_PROXY}, and a subclass of {@link ServerMessageHandler} if packet flow is
     * {@link PacketFlow#TO_SERVER}.
     * @param packetFlow The packet flow
     * @param id The packet id, must be unique in the packet flow
     * @param handler The handler
     * @throws IllegalArgumentException if the id is already registered
     * @throws IllegalArgumentException if the handler is implementing wrong type for the packet flow
     */
    private Protocol(@NotNull PacketFlow packetFlow, int id, @NotNull T handler) {
        this.packetFlow = packetFlow;
        this.id = (byte) (id & 0xFF);
        this.handler = handler;
        if (packetFlow == PacketFlow.TO_PROXY) {
            if (!(handler instanceof ProxyMessageHandler)) {
                throw new IllegalArgumentException("Handler must be instance of ProxyMessageHandler");
            }
            if (TO_PROXY_BY_ID.containsKey(this.id)) {
                throw new IllegalArgumentException("Duplicate protocol id: " + this.id);
            }
            TO_PROXY_BY_ID.put(this.id, this);
        } else {
            if (!(handler instanceof ServerMessageHandler)) {
                throw new IllegalArgumentException("Handler must be instance of ServerMessageHandler");
            }
            if (TO_SERVER_BY_ID.containsKey(this.id)) {
                throw new IllegalArgumentException("Duplicate protocol id: " + this.id);
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
     * @param sender the packet sender to send the packet from. if null, the packet will always be queued.
     * @param msg the message to send
     * @return true if the message was sent successfully, false otherwise.
     */
    public boolean sendPacket(@Nullable PacketSender sender, @NotNull M msg) {
        if (id != 0 && (sender == null || !sender.isEncrypted())) {
            if (this.getPacketFlow() == PacketFlow.TO_SERVER) {
                throw new IllegalStateException("Cannot send packet " + id + " without encryption");
            }
            // usually it should get executed soon after the encryption is done
            AziPluginMessagingProvider.get().getPacketQueue().add(this, msg);
            Logger.getCurrentLogger().info("Queued packet " + id + " to send after encryption (sender: {})", sender);
            return true;
        }
        if (sender == null) {
            throw new IllegalArgumentException("Cannot send packet " + id + " without sender");
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
            if (AziPluginMessagingConfig.debug) {
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
     * This method is called when a raw packet is received (proxy-side).
     * @param server the server connection
     * @param rawData the data of the packet
     * @throws RuntimeException if the connection is encrypted but cannot decrypt the packet
     * @throws RuntimeException if the packet must be received encrypted but the connection is not encrypted
     */
    public static void handleProxySide(ServerConnection server, byte[] rawData) {
        byte[] data;
        boolean encrypted = server.isEncrypted() || server.consumeEncryptedOnce();
        if (encrypted) {
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
            if (id != 0 && !encrypted) {
                throw new RuntimeException("Packet " + id + " must be received encrypted (server: " + server + ")");
            }
            Protocol<?, ?> protocol = Protocol.getById(PacketFlow.TO_PROXY, id);
            if (protocol == null) {
                Logger.getCurrentLogger().warn(
                        "Received unknown protocol id from server connection {}: {}",
                        server, id);
                return;
            }
            if (AziPluginMessagingConfig.debug) {
                String hex = Integer.toString(id, 16);
                if (hex.length() == 1) hex = '0' + hex;
                Logger.getCurrentLogger().info("Received packet {} (0x{}) from server connection {} (encrypted: {})", id, hex, server, server.isEncrypted());
            }
            if (protocol.packetFlow != PacketFlow.TO_PROXY) {
                throw new AssertionError("Packet " + protocol + " is not proxybound");
            }
            @SuppressWarnings("unchecked")
            ProxyMessageHandler<Message> handler = (ProxyMessageHandler<Message>) protocol.getHandler();
            Message message = handler.read(server, in);
            Objects.requireNonNull(message, "handler.read(in) returned null");
            if (in.available() > 0) {
                Logger.getCurrentLogger().error(
                        "Received extra data after message {} from server connection {}: {} of {} bytes remaining",
                        message, server, in.available(), data.length);
                //throw new RuntimeException("Received extra data after message " + message + " from server connection " + server + ": " + in.available() + " of " + data.length + " bytes remaining");
            }
            handler.handle(server, message);
        } catch (Exception | AssertionError e) {
            Logger.getCurrentLogger().warn("Failed to handle plugin message from " + server, e);
        }
    }

    /**
     * This method is called when a raw packet is received (server-side).
     * @param rawData the data of the packet
     * @throws RuntimeException if the connection is encrypted but cannot decrypt the packet
     * @throws RuntimeException if the packet must be received encrypted but the connection is not encrypted
     */
    public static void handleServerSide(@NotNull PacketSender sender, byte[] rawData) {
        byte[] data;
        boolean encrypted = sender.isEncrypted() || sender.consumeEncryptedOnce();
        if (encrypted) {
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
            if (id != 0 && !encrypted) {
                throw new RuntimeException("Packet " + id + " must be received encrypted (sender: " + sender + ")");
            }
            Protocol<?, ?> protocol = Protocol.getById(PacketFlow.TO_SERVER, id);
            if (protocol == null) {
                Logger.getCurrentLogger().warn("Received unknown protocol id from {}: {}", sender, id);
                return;
            }
            if (AziPluginMessagingConfig.debug) {
                String hex = Integer.toString(id, 16);
                if (hex.length() == 1) hex = '0' + hex;
                Logger.getCurrentLogger().info("Received packet {} (0x{}) from {}", id, hex, sender);
            }
            if (protocol.packetFlow != PacketFlow.TO_SERVER) {
                throw new AssertionError("Packet " + protocol + " is not serverbound");
            }
            @SuppressWarnings("unchecked")
            ServerMessageHandler<Message> handler = (ServerMessageHandler<Message>) protocol.getHandler();
            Message message = handler.read(in);
            Objects.requireNonNull(message, "handler.read(in) returned null");
            if (in.available() > 0) {
                Logger.getCurrentLogger().error(
                        "Received extra data after message {} from {}: {} of {} bytes remaining",
                        message, sender, in.available(), data.length);
                //throw new RuntimeException("Received extra data after message " + message + " from " + sender + ": " + in.available() + " of " + data.length + " bytes remaining");
            }
            handler.handle(sender, message);
        } catch (Exception | AssertionError e) {
            Logger.getCurrentLogger().warn("Failed to handle plugin message from " + sender, e);
        }
    }

    /**
     * Returns the packet flow of this packet.
     * @return the packet flow
     */
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

    /**
     * Returns the protocol data in string form.
     * @return the protocol data
     */
    @Override
    public String toString() {
        return "Protocol{" +
                "id=" + id +
                ", packetFlow=" + packetFlow +
                ", handler=" + handler +
                '}';
    }
}
