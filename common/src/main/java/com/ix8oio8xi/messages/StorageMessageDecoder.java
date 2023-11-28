package com.ix8oio8xi.messages;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * A decoder preventing the fragmentation of messages with encoded files.
 */
public class StorageMessageDecoder extends LengthFieldBasedFrameDecoder {
    public StorageMessageDecoder(int maxFrameLength) {
        super(maxFrameLength, 0, 4);
    }
}
