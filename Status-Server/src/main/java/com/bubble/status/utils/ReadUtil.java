package com.bubble.status.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class ReadUtil {

    public static String readStringFromBuf(ByteBuf byteBuf, int length) {
        byte[] magicNumBytes = new byte[length];
        byteBuf.readBytes(magicNumBytes);
        return new String(magicNumBytes, StandardCharsets.UTF_8);
    }
}
