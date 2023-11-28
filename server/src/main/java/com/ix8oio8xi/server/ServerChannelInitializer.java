package com.ix8oio8xi.server;

import com.ix8oio8xi.commands.CommandRegistry;
import com.ix8oio8xi.config.Config;
import com.ix8oio8xi.messages.StorageMessageDecoder;
import com.ix8oio8xi.server.commands.ServerProcessor;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final CommandRegistry<ServerProcessor> registry;

    public ServerChannelInitializer(CommandRegistry<ServerProcessor> registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new StorageMessageDecoder(Config.MAX_FRAME_LENGTH),
                new CommandHandler(registry));
    }
}
