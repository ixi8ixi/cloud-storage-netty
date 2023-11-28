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
 * Download (from server) processor for server. Processor expect to receive message in following format:
 * <p>
 * <pre>
 * +--------------------+-------------------------+
 * |       Source       |       Destination       |
 * +--------------------+-------------------------+
 * |   string           |   string                |
 * +--------------------+-------------------------+
 * </pre>
 * <p>
 * After processing, the processor sends the response in the format:
 * <pre>
 * +---------------------+------------------+--------------------------+-------------------+
 * |       Success       |       Info       |       !Destination       |       !Data       |
 * +---------------------+------------------+--------------------------+-------------------+
 * |   boolean           |   string         |   string                 |   byte[]          |
 * +---------------------+------------------+--------------------------+-------------------+
 * </pre>
 * where Destination and Data are optional fields.
 */
@CommandProcessor(opCode = 0x3)
public class ServerDownloadProcessor implements ServerProcessor {
    /**
     * Trying to write a file into the message and send with success = true.
     * If something goes wrong, it sends a message with success = false and
     * an explanatory message.
     */
    @Override
    public void process(ByteBuf message, HandlerState state, ChannelHandlerContext ctx) {
        String from = MessageDecoder.decodeString(message);
        String to = MessageDecoder.decodeString(message);

        MessageBuilder messageBuilder = new MessageBuilder(opCode());

        try {
            Path source = Paths.get(from);
            // We expect relatively small-sized files
            byte[] data = Files.readAllBytes(source);
            messageBuilder.addBoolean(true).addString("Send file").addString(to).addBytes(data);
        } catch (IOException | InvalidPathException e) {
            messageBuilder.addBoolean(false).addString("Unable to download file, cause: " + e);
        }
        ctx.channel().writeAndFlush(messageBuilder.build());
    }

    private static byte opCode() {
        return 0x3;
    }
}
