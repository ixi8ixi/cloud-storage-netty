package com.ix8oio8xi.server.commands;

import com.ix8oio8xi.commands.CommandProcessor;
import com.ix8oio8xi.server.HandlerState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Interface for user processors. Every user processor annotated with {@link CommandProcessor}
 * should implement {@link ServerProcessor}
 */
public interface ServerProcessor {
    /**
     * Receives a message and processes it according to the given task. Upon processing,
     * if necessary, makes changes to the {@link HandlerState}.
     *
     * @param message received message
     * @param state   current user state
     * @param ctx     user's channel context
     */
    void process(ByteBuf message, HandlerState state, ChannelHandlerContext ctx);
}
