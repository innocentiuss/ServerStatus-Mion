package com.bubble.status.handler.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bubble.status.config.SpringContext;
import com.bubble.status.model.Result;
import com.bubble.status.model.ServerInfoVo;
import com.bubble.status.service.WebStatusService;
import com.bubble.status.utils.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

@Slf4j
@Component
@ServerEndpoint(value = "/connect")
public class WebSocketHandler {

    WebStatusService webStatusService = SpringContext.getBean(WebStatusService.class);


    @OnMessage
    public String onMsg(String message) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (CheckUtil.isSame(jsonObject.getString("msg"), "get")) {
                return getInfos();
            }
            return "{code:400, msg:\"cannot handle message\"}";
        } catch (Exception e) {
            return "{code:400, msg:\"unknown message\"}";
        }
    }

    private String getInfos() {
        List<ServerInfoVo> voList = webStatusService.getInfosFromRestWeb();
        Result result = new Result(voList, (int) (System.currentTimeMillis() / 1000L));
        return JSON.toJSONString(result);
    }
}
