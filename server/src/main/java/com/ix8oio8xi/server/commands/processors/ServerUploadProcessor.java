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
 * Upload (from client) processor for server. Processor expect to receive message in following format:
 * <p>
 * <pre>
 * +-------------------------+------------------+
 * |       Destination       |       Data       |
 * +-------------------------+------------------+
 * |   string                |   byte[]         |
 * +-------------------------+------------------+
 * </pre>
 * <p>
 * After processing, processor sends the response in the format:
 * <pre>
 * +---------------------+------------------+
 * |       Success       |       Info       |
 * +---------------------+------------------+
 * |   boolean           |   string         |
 * +---------------------+------------------+
 * </pre>
 */
@CommandProcessor(opCode = 0x2)
public class ServerUploadProcessor implements ServerProcessor {
    /**
     * Attempts to save the file to the specified path. Sends an informational message based on the results.
     */
    @Override
    public void process(ByteBuf message, HandlerState state, ChannelHandlerContext ctx) {
        String to = MessageDecoder.decodeString(message);
        try {
            Path destination = Paths.get(to);
            Path outputParent = destination.getParent();
            if (outputParent != null) {
                Files.createDirectories(outputParent);
            }
            int len = MessageDecoder.decodeInt(message);
            byte[] data = MessageDecoder.decodeBytes(len, message);

            // We expect relatively small-sized files
            Files.write(destination, data);
            sendResponse(true, "File has been successfully saved.", ctx);
        } catch (IOException | InvalidPathException e) {
            sendResponse(false, "Unable to save file: " + e, ctx);
        }
    }

    private static byte opCode() {
        return 0x2;
    }

    private void sendResponse(boolean success, String info, ChannelHandlerContext ctx) {
        MessageBuilder messageBuilder = new MessageBuilder(opCode());
        ByteBuf message = messageBuilder.addBoolean(success).addString(info).build();
        ctx.channel().writeAndFlush(message);
    }
}
