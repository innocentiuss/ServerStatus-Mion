package com.bubble.status.controller;


import com.alibaba.fastjson.JSON;
import com.bubble.status.model.Result;
import com.bubble.status.model.ServerInfoVo;
import com.bubble.status.service.WebStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebController {

    @Autowired
    WebStatusService webStatusService;

    @RequestMapping("/json")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public String getInfos() {
        List<ServerInfoVo> voList = webStatusService.getInfosFromRestWeb();
        Result result = new Result(voList, (int) (System.currentTimeMillis() / 1000L));
        return JSON.toJSONString(result);
    }

}
