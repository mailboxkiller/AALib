package xyz.aadev.aalib.common.network.messages;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageHandlerBase<MessageT extends IMessage> implements IMessageHandler<MessageT, IMessage> {

    @Override
    public IMessage onMessage(MessageT message, MessageContext ctx) {
        MessageHandlerBase<MessageT> handler = this;
        IThreadListener listener = this.getThreadListener(ctx);

        listener.addScheduledTask(() -> handler.processMessage(message, ctx));

        return null;
    }

    protected abstract IThreadListener getThreadListener(final MessageContext ctx);

    protected abstract void processMessage(final MessageT message, final MessageContext ctx);
}
