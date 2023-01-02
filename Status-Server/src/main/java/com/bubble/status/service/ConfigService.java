package com.bubble.status.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bubble.status.exceptions.ConfigErrorException;
import com.bubble.status.model.ServerInfo;
import com.bubble.status.utils.ReadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
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

        String jsonString = ReadUtil.readJsonConfig(configFileName);
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

    /**
     * 获取配置文件 返回给前端
     * @return json string
     */
    public String getAllConfigs() {
        String jsonString = ReadUtil.readJsonConfig(configFileName);
        JSONArray serversJson = JSON.parseObject(jsonString).getJSONArray("servers");
        return serversJson.toJSONString();
    }

}
