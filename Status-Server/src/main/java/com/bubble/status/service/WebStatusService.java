package com.bubble.status.service;

import com.bubble.status.model.ServerOnlineInfo;
import com.bubble.status.model.ServerInfoVo;
import com.bubble.status.model.Status;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        List<ServerOnlineInfo> configuredServers = configService.getConfiguredServers();

        List<ServerInfoVo> voList = new ArrayList<>(configuredServers.size());
        long now = System.currentTimeMillis() / 1000;
        for (ServerOnlineInfo server : configuredServers) {
            // 跳过配置中disabled了服务器(不展示)
            if (Boolean.TRUE.equals(server.getDisabled())) continue;
            if (Boolean.FALSE.equals(server.getEnabled())) continue;

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
        if (uptime == null) return "未知";
        long seconds = Long.parseLong(uptime);
        // 小于一天时, 换算成XX小时XX分XX秒
        // 小于1天 (86400秒)
        if (seconds < 86400) {
            long h = seconds / 3600;
            long m = (seconds % 3600) / 60;
            long s = seconds % 60;
            return String.format("%d小时%d分%d秒", h, m, s);
        }

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        return String.format("%d天%d小时", days, hours);
    }

    // 负载保留两位小数
    private void uptimeRound2(ServerInfoVo serverInfoVo) {
        if (serverInfoVo.getLoad1() != null) serverInfoVo.setLoad1(round(serverInfoVo.getLoad1(), 2));
        if (serverInfoVo.getLoad5() != null) serverInfoVo.setLoad5(round(serverInfoVo.getLoad5(), 2));
        if (serverInfoVo.getLoad15() != null) serverInfoVo.setLoad15(round(serverInfoVo.getLoad15(), 2));
    }

    // loss保留一位小数
    private void lossRound1(ServerInfoVo serverInfoVo) {
        if (serverInfoVo.getLoss_189() != null) serverInfoVo.setLoss_189(round(serverInfoVo.getLoss_189(), 1));
        if (serverInfoVo.getLoss_10010() != null) serverInfoVo.setLoss_10010(round(serverInfoVo.getLoss_10010(), 1));
        if (serverInfoVo.getLoss_10086() != null) serverInfoVo.setLoss_10086(round(serverInfoVo.getLoss_10086(), 1));
    }

    private double round(double value, int scale) {
        return new BigDecimal(Double.toString(value))
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
