package com.bubble.status.utils;

import com.bubble.status.exceptions.CommonException;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
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
        URL url = IOUtil.class.getProtectionDomain().getCodeSource().getLocation();
        Path jarPath = null;
        try {
            jarPath = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path jarDir = jarPath.getParent();
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
        URL url = IOUtil.class.getProtectionDomain().getCodeSource().getLocation();
        Path jarPath = null;
        try {
            jarPath = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path jarDir = jarPath.getParent();
        Path externalPath = jarDir.resolve(fileName);

        // 2. 确保父目录存在
        if (externalPath.getParent() != null) {
            Files.createDirectories(externalPath.getParent());
        }

        // 3. 写入文件（使用 UTF-8 编码）
        Files.write(externalPath, toWrite.getBytes(StandardCharsets.UTF_8));
    }
}
