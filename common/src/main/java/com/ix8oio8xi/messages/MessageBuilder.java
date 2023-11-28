package com.ix8oio8xi.messages;

import com.ix8oio8xi.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * A builder class for messages exchanged between the server and the client.
 * <p>
 * The following formats are supported:
 * <pre>
 * +--------------------+-----------------------+-------------------+----------------------+----------------------+
 * |        byte        |        boolean        |        int        |        byte[]        |        string        |
 * +--------------------+-----------------------+-------------------+----------------------+----------------------+
 * |   1 byte           |   1 byte              |   4 bytes         | int length + byte[]  |   byte[] in utf_8    |
 * +--------------------+-----------------------+-------------------+----------------------+----------------------+
 * </pre>
 */
public class MessageBuilder {
    private final ByteBuf result;
    private int messageLength;

    public MessageBuilder(byte code, int bufSize) {
        result = Unpooled.buffer(bufSize);
        result.writeInt(0);
        addByte(code);
    }

    public MessageBuilder(byte code) {
        this(code, Config.DEFAULT_INITIAL_BUFFER_SIZE);
    }

    public MessageBuilder addByte(byte b) {
        result.writeByte(b);
        messageLength += 1;
        return this;
    }

    public MessageBuilder addBoolean(boolean b) {
        messageLength += 1;
        result.writeBoolean(b);
        return this;
    }

    public MessageBuilder addInt(int i) {
        messageLength += 4;
        result.writeInt(i);
        return this;
    }

    public MessageBuilder addBytes(byte[] bytes) {
        int len = bytes.length;
        addInt(len);
        messageLength += len;
        result.writeBytes(bytes);
        return this;
    }

    public MessageBuilder addString(String str) {
        addBytes(str.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public ByteBuf build() {
        result.setInt(0, messageLength);
        return result;
    }
}
