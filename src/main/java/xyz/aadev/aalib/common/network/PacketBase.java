package xyz.aadev.aalib.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketBase implements IMessage {
    public abstract IMessage handleClient(NetHandlerPlayClient netHandler);


    public abstract IMessage handleServer(NetHandlerPlayServer netHandler);

    protected void  writePos(BlockPos pos, ByteBuf byteBuf) {
        byteBuf.writeInt(pos.getX());
        byteBuf.writeInt(pos.getY());
        byteBuf.writeInt(pos.getZ());
    }

    protected BlockPos readPos(ByteBuf byteBuf) {
        int x = byteBuf.readInt();
        int y = byteBuf.readInt();
        int z = byteBuf.readInt();

        return new BlockPos(x, y, z);
    }
}
