package xyz.aadev.aalib.common.network;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageHandlerServer<MessageT extends IMessage> extends MessageHandlerBase<MessageT> {
    @Override
    protected IThreadListener getThreadListener(MessageContext ctx) {
        return (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
    }
}
