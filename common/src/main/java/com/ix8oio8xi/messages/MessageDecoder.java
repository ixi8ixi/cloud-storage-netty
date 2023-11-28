package com.ix8oio8xi.messages;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * A decoder class for messages exchanged between the server and the client.
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
public class MessageDecoder {
    private MessageDecoder() {
        // no instances
        throw new AssertionError();
    }

    public static byte decodeByte(ByteBuf buf) {
        return buf.readByte();
    }

    public static int decodeInt(ByteBuf buf) {
        return buf.readInt();
    }

    public static boolean decodeBoolean(ByteBuf buf) {
        return buf.readBoolean();
    }

    public static byte[] decodeBytes(int len, ByteBuf buf) {
        byte[] data = new byte[len];
        buf.readBytes(data, 0, len);
        return data;
    }

    public static String decodeString(ByteBuf buf) {
        int length = decodeInt(buf);
        byte[] data = new byte[length];
        buf.readBytes(data, 0, length);
        return new String(data, StandardCharsets.UTF_8);
    }
}
