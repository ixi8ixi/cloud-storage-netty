package com.ix8oio8xi.server.commands.processors;

import com.ix8oio8xi.commands.CommandProcessor;
import com.ix8oio8xi.messages.MessageBuilder;
import com.ix8oio8xi.messages.MessageDecoder;
import com.ix8oio8xi.server.HandlerState;
import com.ix8oio8xi.server.commands.ServerProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File move processor for server. Processor expect to receive message in following format:
 * <p>
 * +--------------------+-------------------------+
 * |       Source       |       Destination       |
 * +--------------------+-------------------------+
 * |   string           |   string                |
 * +--------------------+-------------------------+
 * <p>
 * After processing, the processor sends the response in the format:
 * +---------------------+------------------+
 * |       Success       |       Info       |
 * +---------------------+------------------+
 * |   byte              |   string         |
 * +---------------------+------------------+
 */
@CommandProcessor(opCode = 0x4)
public class ServerMoveProcessor implements ServerProcessor {
    /**
     * Attempts to move the file from 'source' to 'destination'. Sends an informational
     * message to the client based on the results.
     */
    @Override
    public void process(ByteBuf message, HandlerState state, ChannelHandlerContext ctx) {
        String from = MessageDecoder.decodeString(message);
        String to = MessageDecoder.decodeString(message);

        MessageBuilder messageBuilder = new MessageBuilder(opCode());

        try {
            Path source = Paths.get(from);
            Path destination = Paths.get(to);

            // We expect relatively small-sized files
            byte[] data = Files.readAllBytes(source);
            Path outputParent = destination.getParent();
            if (outputParent != null) {
                Files.createDirectories(outputParent);
            }
            Files.write(destination, data);
            messageBuilder.addBoolean(true).addString("Successfully moved!");
        } catch (IOException | InvalidPathException e) {
            messageBuilder.addBoolean(false).addString("Error while move: " + e);
        }
        ctx.channel().writeAndFlush(messageBuilder.build());
    }

    private static byte opCode() {
        return 0x4;
    }
}
