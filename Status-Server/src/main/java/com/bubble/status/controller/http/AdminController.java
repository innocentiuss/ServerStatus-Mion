package com.bubble.status.controller.http;

import com.bubble.status.model.Login;
import com.bubble.status.model.WebResponse;
import com.bubble.status.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AdminController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/doLogin")
    public String login(@RequestBody Login loginInfo, HttpServletResponse httpServletResponse) {
        try {
            return loginService.doLogin(loginInfo, httpServletResponse);
        } catch (Exception e) {
            return new WebResponse("unknown error", 400).toString();
        }
    }

    @PostMapping("/checkLogin")
    public String checkLogin(HttpServletRequest httpServletRequest) {
        try {
            return loginService.checkLogin(httpServletRequest);
        } catch (Exception e) {
            return new WebResponse("unknown error", 400).toString();
        }
    }
}
