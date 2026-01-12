package com.bubble.status.service;


import com.bubble.status.utils.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import com.bubble.status.exceptions.CommonException;
import com.bubble.status.model.ServerConfigInfo;
import com.bubble.status.model.Configs;
import com.bubble.status.model.ServerOnlineInfo;
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
    volatile Map<String, ServerOnlineInfo> configuredServers;

    @Value("${server.config}")
    private String configFileName;

    @Override
    public void afterPropertiesSet() throws Exception {
        configuredServers = new ConcurrentHashMap<>();
        loadConfigs();
    }

    public ServerOnlineInfo getInfoFromUsername(String username) {
        return configuredServers.get(username);
    }

    /**
     * 加载服务器配置信息
     */
    private void loadConfigs() {
        // 为了解决热加载后, 所有online都变成默认的false了
        // 先保留下加载之前的online信息
        Map<String, ServerOnlineInfo> oldMap = configuredServers;
        Map<String, ServerOnlineInfo> newMap = new ConcurrentHashMap<>();

        String jsonString = IOUtil.readJsonConfig(configFileName);
        Configs configsWrapper = JsonUtil.toObject(jsonString, Configs.class);
        List<ServerConfigInfo> configServers = configsWrapper.getServers();

        configServers.forEach(serverConfigInfo -> {
            ServerOnlineInfo serverInfo = new ServerOnlineInfo();
            BeanUtils.copyProperties(serverConfigInfo, serverInfo);

            if (newMap.containsKey(serverInfo.getUsername())) {
                throw new CommonException("配置文件中出现重复username: " + serverInfo.getUsername());
            }


            // 替换时, 先同步下连接信息, 不然的话会影响后面的继续更新数据
            ServerOnlineInfo oldInfo = oldMap.get(serverInfo.getUsername());
            if (oldInfo != null) {
                serverInfo.setOnline(oldInfo.isOnline());
                serverInfo.setHost(oldInfo.getHost());
                serverInfo.setConnectedIP(oldInfo.getConnectedIP());
                serverInfo.setConnectedPort(oldInfo.getConnectedPort());
            }

            newMap.put(serverInfo.getUsername(), serverInfo);
        });
        configuredServers = newMap;
    }

    public synchronized void refreshConfig() throws IOException {
        loadConfigs();
    }

    /**
     * 获取所有配置了的服务器
     *
     * @return 列表
     */
    public List<ServerOnlineInfo> getConfiguredServers() {
        List<ServerOnlineInfo> result = new ArrayList<>(configuredServers.size());
        for (Map.Entry<String, ServerOnlineInfo> entry : configuredServers.entrySet()) {
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
        Configs configsWrapper = JsonUtil.toObject(jsonString, Configs.class);
        List<ServerConfigInfo> servers = configsWrapper.getServers();

        if (servers != null) {
            for (ServerConfigInfo vo : servers) {
                // 处理 enabled/disabled 状态
                if (vo.getDisabled() != null) {
                    vo.setEnabled(!vo.getDisabled());
                } else if (vo.getEnabled() != null) {
                    vo.setDisabled(!vo.getEnabled());
                } else {
                    vo.setEnabled(true);
                    vo.setDisabled(false);
                }
            }
        }
        return JsonUtil.toJson(servers);
    }

    /**
     * 添加单个配置加入到文件中
     * @param serverConfigInfo 待添加信息
     */
    public synchronized void proceedingAddConfig(ServerConfigInfo serverConfigInfo) throws IOException {
        Configs configs = readConfigsFromFile();
        // 添加新设置
        List<ServerConfigInfo> servers = configs.getServers();
        CheckUtil.check(servers != null, "server_config.json格式似乎有问题, 检查一下吧", HttpStatus.INTERNAL_SERVER_ERROR.value());
        CheckUtil.check(serverConfigInfo.getUsername() != null && !serverConfigInfo.getUsername().equals(""), "用户名不能空着的呢", HttpStatus.BAD_REQUEST.value());
        CheckUtil.check(usernameIsNotDuplicated(servers, serverConfigInfo), "配置的用户名重复啦", HttpStatus.BAD_REQUEST.value());
        if (serverConfigInfo.getEnabled() != null) {
            serverConfigInfo.setDisabled(!serverConfigInfo.getEnabled());
        }
        servers.add(serverConfigInfo);

        // 序列化并保存
        IOUtil.writeString2File(JsonUtil.toJson(configs), configFileName);
        refreshConfig();
        log.info("成功添加新服务器配置啦: username=" + serverConfigInfo.getUsername());
    }

    /**
     * 将前端设置的状态保存并应用
     * @param serverConfigInfos 保存目标
     */
    public synchronized void proceedingSaveConfig(List<ServerConfigInfo> serverConfigInfos) throws IOException {
        // 检查不存在空属性
        CheckUtil.check(notExistEmptyAttribute(serverConfigInfos), "有用户名或密码为空, 拒绝修改啦", HttpStatus.BAD_REQUEST.value());
        // 检查没重复username
        CheckUtil.check(usernameIsNotDuplicated(serverConfigInfos), "配置的用户名重复啦", HttpStatus.BAD_REQUEST.value());
        // 序列化成json并写入
        Configs configs = readConfigsFromFile();

        // enabled disabled转换
        serverConfigInfos.forEach(info -> {
            if (info.getEnabled() != null) {
                info.setDisabled(!info.getEnabled());
            }
        });

        configs.setServers(serverConfigInfos);

        IOUtil.writeString2File(JsonUtil.toPrettyJson(configs), configFileName);
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
            configs = JsonUtil.toObject(jsonString, Configs.class);
        } catch (Exception e) {
            throw new CommonException("server_config.json格式似乎有问题, 检查一下吧", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return configs;
    }

    /**
     * 检查配置文件用户名是否重复
     * @param serverConfigInfoList 反序列化后配置List
     * @param serverConfigInfo 待添加的配置
     * @return ture: 没重复; false: 重复了
     */
    private boolean usernameIsNotDuplicated(List<ServerConfigInfo> serverConfigInfoList, ServerConfigInfo serverConfigInfo) {
        for (ServerConfigInfo info : serverConfigInfoList) {
            if (info.getUsername().equals(serverConfigInfo.getUsername())) return false;
        }
        return true;
    }

    /**
     * 检查用户名是否重复
     * @param serverConfigInfoList 现有配置文件, 从json数组反序列化过来的
     * @return ture: 没重复; false: 重复了
     */
    private boolean usernameIsNotDuplicated(List<ServerConfigInfo> serverConfigInfoList) {
        return serverConfigInfoList.stream().map(ServerConfigInfo::getUsername).collect(Collectors.toSet()).size() == serverConfigInfoList.size();
    }

    /**
     * 校验配置文件不存在空用户名和密码
     * @param serverConfigInfoList 现有配置文件, 从json数组反序列化过来的
     * @return ture: 全有值; false: 有空值
     */
    private boolean notExistEmptyAttribute(List<ServerConfigInfo> serverConfigInfoList) {
        for (ServerConfigInfo serverConfigInfo : serverConfigInfoList) {
            if (serverConfigInfo.getUsername() == null || serverConfigInfo.getUsername().equals("") ||
                    serverConfigInfo.getPassword() == null || serverConfigInfo.getPassword().equals("")) return false;
        }
        return true;
    }

}
