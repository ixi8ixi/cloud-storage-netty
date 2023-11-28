package com.ix8oio8xi.client.commands;

import com.ix8oio8xi.client.ui.UiCallback;
import com.ix8oio8xi.commands.CommandProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Interface for client command processors. Every client processor annotated
 * with {@link CommandProcessor} should implement {@link ClientProcessor}.
 * <p>
 * Typically, to create a server request, you will need to implement a
 * static method called makeMessage() that returns a {@link ByteBuf}.
 */
public interface ClientProcessor {
    /**
     * Receives a message and processes it according to the given task. Upon processing,
     * if necessary, reports the results to the given {@link UiCallback}.
     *
     * @param message  received message
     * @param callback callback for current user interface
     * @param ctx      user's channel context
     */
    void process(ByteBuf message, UiCallback callback, ChannelHandlerContext ctx);
}
