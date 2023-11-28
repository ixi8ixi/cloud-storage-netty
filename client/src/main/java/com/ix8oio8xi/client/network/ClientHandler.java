package com.ix8oio8xi.client.network;

import com.ix8oio8xi.client.commands.ClientProcessor;
import com.ix8oio8xi.client.ui.UiCallback;

import com.ix8oio8xi.commands.CommandRegistry;
import com.ix8oio8xi.messages.MessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Custom handler for server login responses.
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private UiCallback callback;
    private final CommandRegistry<ClientProcessor> registry;

    public ClientHandler(CommandRegistry<ClientProcessor> registry) {
        this.registry = registry;
    }

    public void setCallback(UiCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        int ignoreMessageLength = MessageDecoder.decodeInt(msg);
        byte code = MessageDecoder.decodeByte(msg);
        if (callback != null) {
            registry.getProcessor(code).process(msg, callback, ctx);
        }
    }
}
