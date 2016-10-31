package xyz.aadev.aalib.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import xyz.aadev.aalib.common.util.ModContainerHelper;

public class PacketHandlerBase {

    private static SimpleNetworkWrapper NETWORK;

    protected PacketHandlerBase() {
        getNetwork();
    }

    public static synchronized SimpleNetworkWrapper getNetwork() {
        if (NETWORK == null) {
            synchronized (SimpleNetworkWrapper.class) {
                if (NETWORK == null) {
                    NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ModContainerHelper.getModIdFromActiveContainer());
                }
            }
        }
        return NETWORK;
    }
}
