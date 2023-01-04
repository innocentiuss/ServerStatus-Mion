package com.bubble.status.utils;

import cn.hutool.core.io.FileUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class IOUtil {

    public static String readStringFromBuf(ByteBuf byteBuf, int length) {
        byte[] magicNumBytes = new byte[length];
        byteBuf.readBytes(magicNumBytes);
        return new String(magicNumBytes, StandardCharsets.UTF_8);
    }

    public static String readJsonConfig(String jsonFileName) {
        File file = new File(".", jsonFileName);
        String jsonString;
        // 适配从IDE下运行读文件与打成jar后读文件
        try {
            jsonString = FileUtil.readString(jsonFileName, StandardCharsets.UTF_8);
        } catch (Exception e) {
            jsonString = FileUtil.readString(file, StandardCharsets.UTF_8);
        }
        return jsonString;
    }

    public static void writeString2File(String toWrite, String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        outputStreamWriter.write(toWrite);
        outputStreamWriter.close();
    }
}
