package xyz.aadev.aalib.common.network.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xyz.aadev.aalib.common.config.ConfigHandlerBase;
import xyz.aadev.aalib.common.config.ConfigFileBase;
import xyz.aadev.aalib.common.network.PacketBase;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigSyncPacketBase extends PacketBase {
    private List<ConfigFileBase> config;

    public ConfigSyncPacketBase() {
    }

    protected abstract ConfigHandlerBase getConfig();

    @Override
    public IMessage handleClient(NetHandlerPlayClient netHandler) {
        sync();
        return null;
    }

    @Override
    public IMessage handleServer(NetHandlerPlayServer netHandler) {
        throw new UnsupportedOperationException("Packet registered for the wrong side. This is client side only!");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        config = new ArrayList<>();
        getConfig().getConfigFileList().forEach((configFile) -> {
            int length = buf.readInt();
            byte[] data = new byte[length];
            buf.readBytes(data);
            config.add(configFile.loadFromPacket(data));
        });
    }

    @Override
    public void toBytes(ByteBuf buf) {
        getConfig().getConfigFileList().forEach((configFile) -> {
            byte[] data = configFile.getPacketPayload();
            buf.writeInt(data.length);
            buf.writeBytes(data);
        });
    }

    public boolean sync() {
        return ConfigHandlerBase.syncConfig(getConfig(), config);
    }
}
