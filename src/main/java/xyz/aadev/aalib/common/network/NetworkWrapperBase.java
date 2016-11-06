package xyz.aadev.aalib.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkWrapperBase {
    public final SimpleNetworkWrapper network;
    protected final PacketHandlerBase handler;
    private int id = 0;

    public NetworkWrapperBase(String channelName) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        handler = new PacketHandlerBase();
    }

    /**
     * Registers a packet on both client & server
     *
     * @param clazz The class of the Packet
     */
    public void registerPacket(Class<? extends PacketBase> clazz) {
        registerPacketClient(clazz);
        registerPacketServer(clazz);
    }

    /**
     * Register a packet on the client only
     */
    public void registerPacketClient(Class<? extends PacketBase> clazz) {
        registerPacketImp(clazz, Side.CLIENT);
    }

    /**
     * Register a packet on the server only
     */
    public void registerPacketServer(Class<? extends PacketBase> clazz) {
        registerPacketImp(clazz, Side.SERVER);
    }

    private void registerPacketImp(Class<? extends PacketBase> clazz, Side side) {
        network.registerMessage(handler, clazz, id++, side);
    }

    public static class PacketHandlerBase implements IMessageHandler<PacketBase, IMessage> {

        @Override
        public IMessage onMessage(PacketBase message, MessageContext ctx) {
            if (ctx.side == Side.SERVER)
                return message.handleServer(ctx.getServerHandler());

            return message.handleClient(ctx.getClientHandler());
        }
    }
}
