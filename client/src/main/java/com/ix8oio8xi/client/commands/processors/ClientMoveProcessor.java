package com.ix8oio8xi.client.commands.processors;

import com.ix8oio8xi.client.commands.ClientProcessor;
import com.ix8oio8xi.client.ui.UiCallback;
import com.ix8oio8xi.commands.CommandProcessor;
import com.ix8oio8xi.messages.MessageBuilder;
import com.ix8oio8xi.messages.MessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Login processor for client. {@link ClientMoveProcessor#makeMessage(String, String)}
 * makes message in following format (with header):
 * <pre>
 * +-----------------------+---------------------+--------------------+-------------------------+
 * |       (h)Length       |       (h)Code       |       Source       |       Destination       |
 * +-----------------------+---------------------+--------------------+-------------------------+
 * |   int (4 byte)        |   0x1 (byte)        |   string           |   string                |
 * +-----------------------+---------------------+--------------------+-------------------------+
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
@CommandProcessor(opCode = 0x4)
public class ClientMoveProcessor implements ClientProcessor {
    public static ByteBuf makeMessage(String src, String dst) {
        return new MessageBuilder(opCode()).addString(src).addString(dst).build();
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
        return 0x4;
    }
}
