package net.azisaba.azipluginmessaging.api.protocol;

import net.azisaba.azipluginmessaging.api.Logger;
import net.azisaba.azipluginmessaging.api.protocol.handler.GiveGamingSaraHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.MessageHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.SetRankHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.ToggleGamingSaraHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.ToggleSaraHideHandler;
import net.azisaba.azipluginmessaging.api.protocol.handler.ToggleSaraShowHandler;
import net.azisaba.azipluginmessaging.api.protocol.message.Message;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.PlayerWithServerMessage;
import net.azisaba.azipluginmessaging.api.protocol.message.SetRankMessage;
import net.azisaba.azipluginmessaging.api.server.PacketSender;
import net.azisaba.azipluginmessaging.api.server.ServerConnection;
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
    private static final Map<Byte, Protocol<?, ?>> BY_ID = new ConcurrentHashMap<>();
    public static final String LEGACY_CHANNEL_ID = "AziPluginMessaging";
    public static final String CHANNEL_ID = "azipm:main";

    public static final Protocol<SetRankHandler, SetRankMessage> SET_RANK = new Protocol<>(0x00, new SetRankHandler());
    public static final Protocol<GiveGamingSaraHandler, PlayerMessage> GIVE_GAMING_SARA = new Protocol<>(0x01, new GiveGamingSaraHandler());
    public static final Protocol<ToggleGamingSaraHandler, PlayerMessage> TOGGLE_GAMING_SARA = new Protocol<>(0x02, new ToggleGamingSaraHandler());
    public static final Protocol<ToggleSaraHideHandler, PlayerMessage> TOGGLE_SARA_HIDE = new Protocol<>(0x03, new ToggleSaraHideHandler()); // Note that this is non-contextual
    public static final Protocol<ToggleSaraShowHandler, PlayerWithServerMessage> TOGGLE_SARA_SHOW = new Protocol<>(0x04, new ToggleSaraShowHandler()); // Note that this is contextual

    private final byte id;
    private final T handler;

    private Protocol(int id, @NotNull T handler) {
        this.id = (byte) (id & 0xFF);
        this.handler = handler;
        if (BY_ID.containsKey(this.id)) {
            throw new AssertionError("Duplicate protocol id: " + this.id);
        }
        BY_ID.put(this.id, this);
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
     * @param packetSender the packet sender to send the packet from.
     * @param msg the message to send
     * @return true if the message was sent successfully, false otherwise.
     */
    public boolean sendPacket(@NotNull PacketSender packetSender, @NotNull M msg) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(bout)) {
            out.writeByte(id);
            msg.write(out);
            byte[] bytes = bout.toByteArray();
            return packetSender.sendPacket(bytes);
        } catch (IOException e) {
            Logger.getCurrentLogger().warn("Failed to send packet", e);
            return false;
        }
    }

    /**
     * This method is called when a packet is received (proxy-side).
     * @param server the server connection
     * @param data the data of the packet
     */
    public static void handleProxySide(ServerConnection server, byte[] data) {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(data);
             DataInputStream in = new DataInputStream(bin)) {
            byte id = (byte) (in.readByte() & 0xFF);
            Protocol<?, ?> protocol = Protocol.getById(id);
            if (protocol == null) {
                Logger.getCurrentLogger().warn(
                        "Received unknown protocol id from server connection {}: {}",
                        server, id);
                return;
            }
            String hex = Integer.toString(id, 16);
            if (hex.length() == 1) hex = '0' + hex;
            Logger.getCurrentLogger().info("Received packet {} (0x{}) from server connection {}",
                    id, hex, server);
            @SuppressWarnings("unchecked")
            MessageHandler<Message> handler = (MessageHandler<Message>) protocol.getHandler();
            Message message = handler.read(server, in);
            handler.handle(message);
        } catch (Exception | AssertionError e) {
            Logger.getCurrentLogger().warn(
                    "Failed to handle plugin message from server connection {} (player: {})",
                    server.getServerInfo().getName(), server.getPlayer().getUniqueId(), e);
        }
    }

    /**
     * Gets the protocol (packet) by its id.
     * @param id the id
     * @return the protocol, or null if not found
     */
    @Nullable
    public static Protocol<?, ?> getById(byte id) {
        return BY_ID.get(id);
    }

    /**
     * Returns all the protocols.
     * @return list of protocols
     */
    @Contract(pure = true)
    @NotNull
    public static Collection<Protocol<?, ?>> values() {
        return BY_ID.values();
    }
}

