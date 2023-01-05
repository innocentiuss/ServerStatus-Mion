package com.bubble.status.utils;

import com.bubble.status.exceptions.CommonException;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class IOUtil {

    public static String readStringFromBuf(ByteBuf byteBuf, int length) {
        byte[] magicNumBytes = new byte[length];
        byteBuf.readBytes(magicNumBytes);
        return new String(magicNumBytes, StandardCharsets.UTF_8);
    }

    public static String readJsonConfig(String jsonFileName) {
        Path filePath = Paths.get(jsonFileName);
        byte[] fileBytes;
        try {
            // 文件不存在时会先创建一个模板
            if (!Files.exists(filePath)) {
                byte[] templateBytes = Files.readAllBytes(Paths.get("src/main/resources/server_config.json"));
                String templateContent = new String(templateBytes, StandardCharsets.UTF_8);
                Files.write(filePath, templateContent.getBytes(StandardCharsets.UTF_8));
            }
            fileBytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    public static void writeString2File(String toWrite, String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        outputStreamWriter.write(toWrite);
        outputStreamWriter.close();
    }
}
