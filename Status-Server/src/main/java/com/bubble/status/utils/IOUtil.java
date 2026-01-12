package com.bubble.status.utils;

import com.bubble.status.exceptions.CommonException;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class IOUtil {

    public static String readStringFromBuf(ByteBuf byteBuf, int length) {
        byte[] magicNumBytes = new byte[length];
        byteBuf.readBytes(magicNumBytes);
        return new String(magicNumBytes, StandardCharsets.UTF_8);
    }

    public static String readJsonConfig(String jsonFileName) {
        // 1. 定位外部文件的绝对路径（默认相对于执行程序的当前工作目录）
        String jarPath = IOUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        Path jarDir = Paths.get(jarPath).getParent();
        Path externalPath = jarDir.resolve(jsonFileName);

        try {
            // 2. 如果外部文件不存在，则从 JAR 内部资源中复制一份出来
            if (!Files.exists(externalPath)) {
                // 使用 ClassLoader 获取资源流，这在 IDE 和 JAR 中都通用
                // 注意：这里的路径是相对于 resources 根目录的，不需要写 src/main/resources
                try (InputStream is = IOUtil.class.getClassLoader().getResourceAsStream(jsonFileName)) {
                    if (is == null) {
                        throw new RuntimeException("在 Resources 目录中未找到模板文件: " + jsonFileName);
                    }

                    // 如果外部目录也不存在（例如 jsonFileName 包含子目录），先创建父目录
                    if (externalPath.getParent() != null) {
                        Files.createDirectories(externalPath.getParent());
                    }

                    // 将资源流拷贝到外部文件系统
                    Files.copy(is, externalPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("已创建默认配置文件: " + externalPath);
                }
            }

            // 3. 读取外部文件内容
            byte[] fileBytes = Files.readAllBytes(externalPath);
            return new String(fileBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new CommonException("加载配置文件失败: " + e.getMessage());
        }
    }

    public static void writeString2File(String toWrite, String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        outputStreamWriter.write(toWrite);
        outputStreamWriter.close();
    }
}
