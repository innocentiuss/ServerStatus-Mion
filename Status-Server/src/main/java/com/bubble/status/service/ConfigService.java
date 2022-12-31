package com.bubble.status.service;


import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bubble.status.exceptions.ConfigErrorException;
import com.bubble.status.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
// 已配置的服务器相关信息
public class ConfigService implements InitializingBean {

    // 保存所有服务器连接信息
    // key: service username
    Map<String, ServerInfo> configuredServers;

    @Value("${server.config}")
    private String configFileName;

    @Override
    public void afterPropertiesSet() throws Exception {
        readConfigs();
    }

    public ServerInfo getInfoFromUsername(String username) {
        return configuredServers.get(username);
    }

    /**
     * 加载服务器配置信息
     */
    private void readConfigs() throws IOException {
        configuredServers = new ConcurrentHashMap<>();

        File file = new File(".", configFileName);
        String jsonString;
        // 适配从IDE下运行读文件与打成jar后读文件
        try {
            jsonString = FileUtil.readString(configFileName, StandardCharsets.UTF_8);
        } catch (Exception e) {
            jsonString = FileUtil.readString(file, StandardCharsets.UTF_8);
        }
        JSONArray serversJson = JSON.parseObject(jsonString).getJSONArray("servers");

        for (int i = 0; i < serversJson.size(); i++) {
            ServerInfo serverInfo = serversJson.getObject(i, ServerInfo.class);

            if (configuredServers.containsKey(serverInfo.getUsername()))
                throw new ConfigErrorException("配置信息中出现重复username!");

            configuredServers.put(serverInfo.getUsername(), serverInfo);
        }
    }

    public void refreshConfig() throws IOException {
        readConfigs();
    }

    /**
     * 获取所有配置了的服务器
     *
     * @return 列表
     */
    public List<ServerInfo> getConfiguredServers() {
        List<ServerInfo> result = new ArrayList<>(configuredServers.size());
        for (Map.Entry<String, ServerInfo> entry : configuredServers.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

}
