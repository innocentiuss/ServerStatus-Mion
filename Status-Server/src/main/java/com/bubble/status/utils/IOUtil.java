package com.bubble.status.utils;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

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
        try {
            // 1. 定位外部文件的绝对路径
            Path baseDir = getBasePath();
            Path externalPath = baseDir.resolve(jsonFileName);

            // 2. 如果外部文件不存在，则从 JAR 内部资源中复制一份出来
            if (!Files.exists(externalPath)) {
                // 使用 ClassLoader 获取资源流
                try (InputStream is = IOUtil.class.getClassLoader().getResourceAsStream(jsonFileName)) {
                    if (is == null) {
                        throw new RuntimeException("在 Resources 目录中未找到模板文件: " + jsonFileName);
                    }

                    // 确保父目录存在
                    if (externalPath.getParent() != null) {
                        Files.createDirectories(externalPath.getParent());
                    }

                    // 将资源流拷贝到外部文件系统
                    Files.copy(is, externalPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("已创建默认配置文件: " + externalPath.toAbsolutePath());
                }
            }

            // 3. 读取外部文件内容
            byte[] fileBytes = Files.readAllBytes(externalPath);
            return new String(fileBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            // 这里建议捕获特定的异常或者自定义异常
            throw new RuntimeException("加载配置文件失败: " + e.getMessage(), e);
        }
    }

    public static void writeString2File(String toWrite, String fileName) throws IOException {
        // 1. 定位外部文件的绝对路径
        Path baseDir = getBasePath();
        Path externalPath = baseDir.resolve(fileName);

        // 2. 确保父目录存在
        if (externalPath.getParent() != null) {
            Files.createDirectories(externalPath.getParent());
        }

        // 3. 写入文件（使用 UTF-8 编码）
        Files.write(externalPath, toWrite.getBytes(StandardCharsets.UTF_8));
    }

    private static Path getBasePath() {
        // ApplicationHome 会自动处理 IDE 和 JAR 模式的差异，直接找到物理 jar 文件或 classes 目录
        ApplicationHome home = new ApplicationHome(IOUtil.class);
        File source = home.getSource();

        // 如果 source 为 null (极少见)，回退到当前工作目录
        if (source == null) {
            return Paths.get(".").toAbsolutePath();
        }

        // getSource() 返回的是 app.jar 本身，我们需要它的父目录
        return source.getParentFile().toPath();
    }
}
