package com.bubble.status.service;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.bubble.status.model.ServerInfo;
import com.bubble.status.model.ServerInfoVo;
import com.bubble.status.model.Status;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
// web前端返回结果使用
public class WebStatusService {

    // 先获取所有已经配置了的服务器
    // 再对认证的注入最新的属性

    // 没有认证的, 注入默认值
    @Autowired
    ConfigService configService;

    @Autowired
    StatusService statusService;

    @Value("${server.disconnected.timeout}")
    Long disconnectTimeout;

    public List<ServerInfoVo> getInfosFromRestWeb() {
        List<ServerInfo> configuredServers = configService.getConfiguredServers();

        List<ServerInfoVo> voList = new ArrayList<>(configuredServers.size());
        long now = System.currentTimeMillis() / 1000;
        for (ServerInfo server : configuredServers) {
            // 跳过配置中disabled了服务器
            if (server.isDisabled()) continue;

            // 先设置初始属性
            ServerInfoVo serverInfoVo = new ServerInfoVo();
            boolean online = server.isOnline();
            serverInfoVo.setType(server.getType());
            serverInfoVo.setName(server.getName());
            serverInfoVo.setLocation(server.getLocation());
            serverInfoVo.setOnline(online);
            serverInfoVo.setRegion(server.getRegion());

            // 满足已经认证过的并且最新数据小于阈值的才返回结果
            if (online) {
                Status status = statusService.getStatus(server.getConnectedIP(), server.getConnectedPort());
                if (status != null && now - status.getNow() <= disconnectTimeout) {
                    BeanUtils.copyProperties(status, serverInfoVo);
                    serverInfoVo.setUptime(uptimeSeconds2Day(serverInfoVo.getUptime()));
                    uptimeRound2(serverInfoVo);
                    lossRound1(serverInfoVo);
                }
                else {
                    serverInfoVo.setLost(true);
                }
            }
            voList.add(serverInfoVo);
        }
        return voList;

    }

    // 客户端传来的时间为秒数, 这里换算成天数
    private String uptimeSeconds2Day(String uptime) {
        int time = Integer.parseInt(uptime);
        // 小于一天时, 换算成XX小时XX分XX秒
        if (time < 86400)
            return DateUtil.formatBetween((long) time * 1000L, BetweenFormatter.Level.SECOND);

        return DateUtil.formatBetween((long) time * 1000L, BetweenFormatter.Level.HOUR);
    }

    // 负载保留两位小数
    private void uptimeRound2(ServerInfoVo serverInfoVo) {
        serverInfoVo.setLoad1(NumberUtil.round(serverInfoVo.getLoad1(), 2).doubleValue());
        serverInfoVo.setLoad5(NumberUtil.round(serverInfoVo.getLoad5(), 2).doubleValue());
        serverInfoVo.setLoad15(NumberUtil.round(serverInfoVo.getLoad15(), 2).doubleValue());
    }

    // loss保留一位小数
    private void lossRound1(ServerInfoVo serverInfoVo) {
        serverInfoVo.setLoss_189(NumberUtil.round(serverInfoVo.getLoss_189(), 1).doubleValue());
        serverInfoVo.setLoss_10010(NumberUtil.round(serverInfoVo.getLoss_10010(), 1).doubleValue());
        serverInfoVo.setLoss_10086(NumberUtil.round(serverInfoVo.getLoss_10086(), 1).doubleValue());
    }
}
