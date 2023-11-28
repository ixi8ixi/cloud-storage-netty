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
 * Download processor for client. {@link ClientDownloadProcessor#makeMessage(String, String)}
 * makes message in following format (with header):
 * <pre>
 * +-----------------------+---------------------+--------------------+-------------------------+
 * |       (h)Length       |       (h)Code       |       Source       |       Destination       |
 * +-----------------------+---------------------+--------------------+-------------------------+
 * |   int (4 byte)        |   0x3 (byte)        |   string           |   string                |
 * +-----------------------+---------------------+--------------------+-------------------------+
 * </pre>
 * <p>
 * Response is expected in format:
 * <pre>
 * +---------------------+------------------+--------------------------+-------------------+
 * |       Success       |       Info       |       !Destination       |       !Data       |
 * +---------------------+------------------+--------------------------+-------------------+
 * |   boolean           |   string         |   string                 |   byte[]          |
 * +---------------------+------------------+--------------------------+-------------------+
 * </pre>
 * Where !Destination and !Data are optional fields.
 */
@CommandProcessor(opCode = 0x3)
public class ClientDownloadProcessor implements ClientProcessor {
    public static ByteBuf makeMessage(String serverSrc, String clientDst) {
        MessageBuilder messageBuilder = new MessageBuilder(opCode());
        messageBuilder.addString(serverSrc).addString(clientDst);
        return messageBuilder.build();
    }

    /**
     * Checks if the download has been successfully completed. If yes, it saves the file.
     * Otherwise, it displays an error message.
     */
    @Override
    public void process(ByteBuf message, UiCallback callback, ChannelHandlerContext ctx) {
        boolean success = MessageDecoder.decodeBoolean(message);
        String info = MessageDecoder.decodeString(message);

        if (!success) {
            callback.postMessage(false, info);
            return;
        }

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
            callback.postMessage(true, "File downloaded successfully");
        } catch (IOException | InvalidPathException e) {
            callback.postMessage(false, "Unable to save file: " + e);
        }
    }

    private static byte opCode() {
        return 0x3;
    }
}
