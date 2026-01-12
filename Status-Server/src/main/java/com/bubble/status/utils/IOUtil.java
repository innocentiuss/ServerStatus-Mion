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

    /**
     * 获取当前执行环境的基准目录
     * 1. IDEA 运行时：通常指向 target 目录 (或者 target/classes 的父级)
     * 2. Jar 运行时：指向 Jar 包同级目录
     */
    private static Path getBasePath() throws IOException {
        try {
            // 关键点 1: 获取 CodeSource 的 URL
            URL url = IOUtil.class.getProtectionDomain().getCodeSource().getLocation();

            // 关键点 2: 使用 toURI() 解决 Windows 下 /D:/... 的路径问题以及空格(%20)问题
            Path path = Paths.get(url.toURI());

            // 关键点 3: 判断是目录(IDE)还是文件(Jar)
            // 如果是文件(xx.jar)，直接取父目录
            // 如果是目录(target/classes)，也可以取父目录(target)作为基准，防止配置文件写到 classes 里面去
            if (Files.isDirectory(path)) {
                return path.getParent(); // 在 IDE 中通常返回 target 目录
            }
            return path.getParent(); // 在 Jar 模式下返回 Jar 包所在的目录

        } catch (URISyntaxException e) {
            throw new IOException("无法解析程序路径", e);
        }
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
}
