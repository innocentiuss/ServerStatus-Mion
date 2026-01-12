package com.bubble.status.service;

import com.bubble.status.model.Status;
import com.bubble.status.utils.JsonUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
// handler更新服务器信息使用
public class StatusService implements InitializingBean {

    // 保存所有服务器连接信息
    // key: ip+port
    private Map<String, Status> onlineServerStatus;


    @Override
    public void afterPropertiesSet() throws Exception {
        onlineServerStatus = new ConcurrentHashMap<>();
    }

    public boolean updateStatus(String ip, int port, String statusJsonString) {
        Status status = JsonUtil.toObject(statusJsonString, Status.class);
        String key = ip + port;
        onlineServerStatus.put(key, status);
        return true;
    }

    public Status getStatus(String ip, int port) {
        return onlineServerStatus.get(ip + port);
    }
}
