package com.bubble.status.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @RequestMapping("/ping")
    public String healthCheck() {
        return "pong";
    }
}
