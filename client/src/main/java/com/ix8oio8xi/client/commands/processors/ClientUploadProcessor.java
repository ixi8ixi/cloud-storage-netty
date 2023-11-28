package com.ix8oio8xi.client.commands.processors;

import com.ix8oio8xi.client.commands.ClientProcessor;
import com.ix8oio8xi.client.ui.UiCallback;
import com.ix8oio8xi.commands.CommandProcessor;
import com.ix8oio8xi.messages.MessageBuilder;
import com.ix8oio8xi.messages.MessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Upload processor for client. {@link ClientUploadProcessor#makeMessage(String, String)}
 * makes message in following format (with header):
 * <pre>
 * +-----------------------+---------------------+-------------------------+------------------+
 * |       (h)Length       |       (h)Code       |       Destination       |       Data       |
 * +-----------------------+---------------------+-------------------------+------------------+
 * |   int (4 byte)        |   0x3 (byte)        |   string                |   byte[]         |
 * +-----------------------+---------------------+-------------------------+------------------+
 * </pre>
 * <p>
 * Response is expected in format:
 * <pre>
 * +---------------------+------------------+
 * |       Success       |       Info       |
 * +---------------------+------------------+
 * |   boolean           |   string         |
 * +---------------------+------------------+
 * </pre>
 */
@CommandProcessor(opCode = 0x2)
public class ClientUploadProcessor implements ClientProcessor {
    /**
     * Makes upload request to server with source file data added.
     *
     * @throws InvalidPathException if from string cannot be converted to the Path
     * @throws IOException          if problems occurred during file reading
     */
    public static ByteBuf makeMessage(String from, String to) throws IOException {
        Path source = Paths.get(from);
        MessageBuilder messageBuilder = new MessageBuilder(opCode()).addString(to);

        // We expect relatively small-sized files
        byte[] data = Files.readAllBytes(source);
        messageBuilder.addBytes(data);
        return messageBuilder.build();
    }

    /**
     * Just posts a message from the server to the user interface.
     */
    @Override
    public void process(ByteBuf message, UiCallback callback, ChannelHandlerContext ctx) {
        boolean success = MessageDecoder.decodeBoolean(message);
        String info = MessageDecoder.decodeString(message);

        callback.postMessage(success, info);
    }

    private static byte opCode() {
        return 0x2;
    }
}
