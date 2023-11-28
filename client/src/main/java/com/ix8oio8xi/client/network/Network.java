package com.ix8oio8xi.client.network;

import com.ix8oio8xi.client.commands.ClientProcessor;
import com.ix8oio8xi.client.ui.UiCallback;
import com.ix8oio8xi.commands.CommandRegistry;
import com.ix8oio8xi.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controller class for Netty client. The client is launched in a separate thread to avoid blocking the UI.
 */
public class Network {
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final CountDownLatch sync = new CountDownLatch(1);
    private ClientChannelInitializer initializer;
    private UiCallback callback;

    /**
     * Configure and run a Netty client on a separate thread.
     * Sets the "started" flag to true when the client is launched and the connection is established.
     */
    public Network() {
        CommandRegistry<ClientProcessor> registry = new CommandRegistry<>(Config.CLIENT_PROCESSORS_PACKAGE);
        registry.init();
        service.submit(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                initializer = new ClientChannelInitializer(registry);
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(initializer);
                ChannelFuture future = bootstrap.connect(Config.HOST, Config.PORT).sync();
                sync.countDown();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                initializer.channel().close();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
    }

    public void shutdown() {
        service.shutdownNow();
    }

    public void setCallback(UiCallback callback) {
        this.callback = callback;
        ClientHandler handler = initializer.channel().pipeline().get(ClientHandler.class);
        if (handler != null) {
            handler.setCallback(callback);
        }
    }

    public boolean sync(int timeout) throws InterruptedException {
        return sync.await(timeout, TimeUnit.SECONDS);
    }

    public boolean started() {
        return sync.getCount() == 0;
    }

    public void sendMessage(ByteBuf buf) {
        // We can't send messages if the network connection is not yet established
        if (started()) {
            initializer.channel().writeAndFlush(buf);
        } else if (callback != null) {
            callback.postMessage(false, "The connection has not been established yet.");
        }
    }
}
