package com.ix8oio8xi.server;

import com.ix8oio8xi.commands.CommandRegistry;
import com.ix8oio8xi.log.Log;
import com.ix8oio8xi.server.commands.ServerProcessor;
import com.ix8oio8xi.config.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Server application for cloud storage.
 * The launch occurs in the main thread, and the logs are printed to the console.
 */
public class ServerApplication {
    public static void main(String[] args) {
        Log.system("Run application");
        CommandRegistry<ServerProcessor> registry = new CommandRegistry<>(Config.SERVER_PROCESSORS_PACKAGE);
        Log.system("Init command registry");
        registry.init();
        Log.system(registry.toString());

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer(registry));
            Log.system("Start server");
            ChannelFuture future = bootstrap.bind(Config.PORT).sync();
            Log.system("Server is working");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Log.error("Main thread interruption caught");
        } finally {
            Log.system("Stop server");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            Log.system("Server has been stopped");
        }
    }
}
