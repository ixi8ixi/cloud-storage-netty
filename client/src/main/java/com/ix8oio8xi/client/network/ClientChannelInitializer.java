package com.ix8oio8xi.client.network;

import com.ix8oio8xi.client.commands.ClientProcessor;
import com.ix8oio8xi.commands.CommandRegistry;
import com.ix8oio8xi.config.Config;
import com.ix8oio8xi.messages.StorageMessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final CommandRegistry<ClientProcessor> registry;
    private SocketChannel channel;

    public ClientChannelInitializer(CommandRegistry<ClientProcessor> registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        channel = socketChannel;
        socketChannel.pipeline().addLast(new StorageMessageDecoder(Config.MAX_FRAME_LENGTH),
                new ClientHandler(registry));
    }

    public SocketChannel channel() {
        return channel;
    }
}
