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
import java.util.stream.Collectors;

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
        loadConfigs();
    }

    public ServerInfo getInfoFromUsername(String username) {
        return configuredServers.get(username);
    }

    /**
     * 加载服务器配置信息
     */
    private void loadConfigs() {
        configuredServers = new ConcurrentHashMap<>();

        String jsonString = IOUtil.readJsonConfig(configFileName);
        JSONArray serversJson = JSON.parseObject(jsonString).getJSONArray("servers");

        for (int i = 0; i < serversJson.size(); i++) {
            ServerInfo serverInfo = serversJson.getObject(i, ServerInfo.class);

            if (configuredServers.containsKey(serverInfo.getUsername()))
                throw new CommonException("配置文件中出现重复username, 检查一下吧");

            configuredServers.put(serverInfo.getUsername(), serverInfo);
        }
    }

    public synchronized void refreshConfig() throws IOException {
        loadConfigs();
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

    /**
     * 添加单个配置加入到文件中
     * @param configInfo 待添加信息
     */
    public synchronized void proceedingAddConfig(ConfigInfo configInfo) throws IOException {
        Configs configs = readConfigsFromFile();
        // 添加新设置
        List<ConfigInfo> servers = configs.getServers();
        CheckUtil.check(servers != null, "server_config.json格式似乎有问题, 检查一下吧", HttpStatus.HTTP_INTERNAL_ERROR);
        CheckUtil.check(configInfo.getUsername() != null && !configInfo.getUsername().equals(""), "用户名不能空着的呢", HttpStatus.HTTP_BAD_REQUEST);
        CheckUtil.check(usernameIsNotDuplicated(servers, configInfo), "配置的用户名重复啦", HttpStatus.HTTP_BAD_REQUEST);
        servers.add(configInfo);

        // 序列化并保存
        IOUtil.writeString2File(JSON.toJSONString(configs), configFileName);
        refreshConfig();
        log.info("成功添加新服务器配置啦: username=" + configInfo.getUsername());
    }

    /**
     * 将前端设置的状态保存并应用
     * @param configInfos 保存目标
     */
    public synchronized void proceedingSaveConfig(List<ConfigInfo> configInfos) throws IOException {
        // 检查不存在空属性
        CheckUtil.check(notExistEmptyAttribute(configInfos), "有用户名或密码为空, 拒绝修改啦", HttpStatus.HTTP_BAD_REQUEST);
        // 检查没重复username
        CheckUtil.check(usernameIsNotDuplicated(configInfos), "配置的用户名重复啦", HttpStatus.HTTP_BAD_REQUEST);
        // 序列化成json并写入
        Configs configs = readConfigsFromFile();
        configs.setServers(configInfos);

        IOUtil.writeString2File(JSON.toJSONString(configs), configFileName);
        refreshConfig();
        log.info("保存服务器配置成功啦");
    }

    /**
     * 从配置文件读取并反序列化
     * @return 配置信息实体类
     */
    private Configs readConfigsFromFile() {
        // 读取现有config文件
        String jsonString = IOUtil.readJsonConfig(configFileName);
        // 反序列化
        Configs configs;
        try {
            configs = JSON.parseObject(jsonString, Configs.class);
        } catch (Exception e) {
            throw new CommonException("server_config.json格式似乎有问题, 检查一下吧", HttpStatus.HTTP_INTERNAL_ERROR);
        }
        return configs;
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

    /**
     * 检查用户名是否重复
     * @param configInfoList 现有配置文件, 从json数组反序列化过来的
     * @return ture: 没重复; false: 重复了
     */
    private boolean usernameIsNotDuplicated(List<ConfigInfo> configInfoList) {
        return configInfoList.stream().map(ConfigInfo::getUsername).collect(Collectors.toSet()).size() == configInfoList.size();
    }

    /**
     * 校验配置文件不存在空用户名和密码
     * @param configInfoList 现有配置文件, 从json数组反序列化过来的
     * @return ture: 全有值; false: 有空值
     */
    private boolean notExistEmptyAttribute(List<ConfigInfo> configInfoList) {
        for (ConfigInfo configInfo : configInfoList) {
            if (configInfo.getUsername() == null || configInfo.getUsername().equals("") ||
                    configInfo.getPassword() == null || configInfo.getPassword().equals("")) return false;
        }
        return true;
    }

}
