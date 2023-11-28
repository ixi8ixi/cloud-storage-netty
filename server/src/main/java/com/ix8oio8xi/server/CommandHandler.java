package com.ix8oio8xi.server;

import com.ix8oio8xi.commands.CommandRegistry;
import com.ix8oio8xi.log.Log;
import com.ix8oio8xi.messages.MessageDecoder;
import com.ix8oio8xi.server.commands.ServerProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CommandHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final CommandRegistry<ServerProcessor> registry;
    private final HandlerState state = new HandlerState();

    public CommandHandler(CommandRegistry<ServerProcessor> registry) {
        this.registry = registry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Log.event("New user connected: " + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Log.event("User inactive: " + ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Log.event("Exception " + cause + " caught " + ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        int messageLength = MessageDecoder.decodeInt(msg);
        byte code = MessageDecoder.decodeByte(msg);
        Log.event("Message with length " + messageLength + " and code " + code + " received on " + ctx);
        registry.getProcessor(code).process(msg, state, ctx);
    }
}
