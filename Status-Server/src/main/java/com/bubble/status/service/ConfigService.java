package com.bubble.status.service;


import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bubble.status.exceptions.CommonException;
import com.bubble.status.model.ConfigInfo;
import com.bubble.status.model.Configs;
import com.bubble.status.model.ServerInfo;
import com.bubble.status.utils.CheckUtil;
import com.bubble.status.utils.IOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
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
    private void readConfigs() {
        configuredServers = new ConcurrentHashMap<>();

        String jsonString = IOUtil.readJsonConfig(configFileName);
        JSONArray serversJson = JSON.parseObject(jsonString).getJSONArray("servers");

        for (int i = 0; i < serversJson.size(); i++) {
            ServerInfo serverInfo = serversJson.getObject(i, ServerInfo.class);

            if (configuredServers.containsKey(serverInfo.getUsername()))
                throw new CommonException("配置信息中出现重复username!");

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
     * 获取配置文件信息 返回给前端
     * @return json string
     */
    public String getAllConfigs() {
        String jsonString = IOUtil.readJsonConfig(configFileName);
        JSONArray serversJson = JSON.parseObject(jsonString).getJSONArray("servers");

        List<ConfigInfo> configs = new ArrayList<>();
        for (int i = 0; i < serversJson.size(); i++) {
            ConfigInfo vo = serversJson.getObject(i, ConfigInfo.class);
            vo.setEnabled(!vo.isDisabled());
            configs.add(vo);
        }

        return JSON.toJSONString(configs);
    }

    public synchronized void proceedingAddConfig(ConfigInfo configInfo) {
        // 读取现有config文件
        String jsonString = IOUtil.readJsonConfig(configFileName);
        // 反序列化
        Configs configs;
        try {
            configs = JSON.parseObject(jsonString, Configs.class);
        } catch (Exception e) {
            throw new CommonException("Json文件序列化错误, 请检查server_config.json格式是否合法", HttpStatus.HTTP_INTERNAL_ERROR);
        }

        // 添加新设置
        List<ConfigInfo> servers = configs.getServers();
        CheckUtil.check(servers != null, "请检查server_config.json格式是否合法", HttpStatus.HTTP_INTERNAL_ERROR);
        CheckUtil.check(usernameIsNotDuplicated(servers, configInfo), "配置的用户名重复了", HttpStatus.HTTP_BAD_REQUEST);
        servers.add(configInfo);

        // 序列化并保存
        IOUtil.writeString2File(JSON.toJSONString(configs), configFileName);
        log.info("成功添加新服务器配置: username=" + configInfo.getUsername());
    }

    /**
     * 检查配置文件用户名是否重复
     * @param configInfoList 反序列化后配置List
     * @param configInfo 待添加的配置
     * @return ture: 没重复; false: 重复了
     */
    private boolean usernameIsNotDuplicated(List<ConfigInfo> configInfoList, ConfigInfo configInfo) {
        for (ConfigInfo info : configInfoList) {
            if (info.getUsername().equals(configInfo.getUsername())) return false;
        }
        return true;
    }

}
