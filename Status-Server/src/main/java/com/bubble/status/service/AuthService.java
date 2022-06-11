package com.bubble.status.service;

import com.alibaba.fastjson.JSONObject;
import com.bubble.status.model.AuthInfo;
import com.bubble.status.model.ServerInfo;
import com.bubble.status.utils.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
// 用于客户端第一建立连接时认证, 保存所有已经连接的服务器
public class AuthService {

    // key: ip+port, 用于记录此次连接ip+port与对应服务器的映射
    Map<String, ServerInfo> authed = new ConcurrentHashMap<>();

    @Autowired
    ConfigService configService;

    /**
     * 第一次连接时执行认证
     * @param connectInfos 接收到的String, 已除去头部内容
     * @param ip 客户端ip
     * @param port 客户端port
     * @return auth is ok
     */
    public boolean doAuth(String connectInfos, String ip, int port) {
        AuthInfo authInfo = JSONObject.parseObject(connectInfos, AuthInfo.class);
        ServerInfo savedInfo = configService.getInfoFromUsername(authInfo.getUsername());
        // 认证失败
        if (CheckUtil.isNotSame(authInfo.getPassword(), savedInfo.getPassword())) return false;
        // 认证成功
        savedInfo.setOnline(true);
        savedInfo.setConnectedIP(ip);
        savedInfo.setConnectedPort(port);
        authed.put(ip + port, savedInfo);
        log.info("服务器认证成功, username: {}, ip: {}", savedInfo.getUsername(), ip);
        return true;
    }

    /**
     * 处理断开的连接
     * @param ip 客户端IP
     * @param port 客户端Port
     */
    public void disConnect(String ip, int port) {
        String key = ip + port;
        ServerInfo info = authed.get(key);
        if (info != null) {
            info.setOnline(false);
            authed.remove(key);
            log.info("服务器断开连接成功, username: {}", info.getUsername());
        }
    }
}
