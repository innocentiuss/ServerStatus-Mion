package com.bubble.status.service;

import com.bubble.status.controller.ws.WebSocketHandler;
import com.bubble.status.model.Result;
import com.bubble.status.model.ServerInfoVo;
import com.bubble.status.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataPushService {
    @Autowired
    private WebStatusService webStatusService;

    @Scheduled(fixedRateString = "${server.push.interval:1000}")
    public void pushServerInfo() {
        if (WebSocketHandler.noActiveSession()) {
            return;
        }
        try {
            List<ServerInfoVo> voList = webStatusService.getInfosFromRestWeb();
            Result result = new Result(voList, (int) (System.currentTimeMillis() / 1000L));

            String jsonMessage = JsonUtil.toJson(result);
            WebSocketHandler.sendToAllClients(jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
