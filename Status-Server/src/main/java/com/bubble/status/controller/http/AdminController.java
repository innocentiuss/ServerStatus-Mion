package com.bubble.status.controller.http;

import com.bubble.status.model.CommonWebResponse;
import org.springframework.http.HttpStatus;
import com.bubble.status.exceptions.CommonException;
import com.bubble.status.exceptions.ConfigErrorException;
import com.bubble.status.model.ServerConfigInfo;
import com.bubble.status.model.Login;
import com.bubble.status.service.ConfigService;
import com.bubble.status.service.LoginService;
import com.bubble.status.utils.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private ConfigService configService;

    @PostMapping("/doLogin")
    public String login(@RequestBody Login loginInfo, HttpServletResponse httpServletResponse) {
        try {
            return loginService.doLogin(loginInfo, httpServletResponse);
        } catch (Exception e) {
            return new CommonWebResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString();
        }
    }

    @PostMapping("/checkLogin")
    public String checkLogin(HttpServletRequest httpServletRequest) {
        try {
            return loginService.checkLogin(httpServletRequest);
        } catch (Exception e) {
            return new CommonWebResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString();
        }
    }

    @GetMapping("/getConfigs")
    public String getConfigs() {
        try {
            return new CommonWebResponse<>(configService.getAllConfigs(), HttpStatus.OK.value()).toString();
        } catch (Exception e) {
            return new CommonWebResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString();
        }
    }

    @GetMapping("/reloadConfigs")
    public String reloadConfigs() {
        try {
            configService.refreshConfig();
        } catch (ConfigErrorException | IOException exception) {
            return new CommonWebResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()).toString();
        }
        return new CommonWebResponse<>(null, HttpStatus.OK.value()).toString();
    }

    @PostMapping("/addConfig")
    public String addConfigs(@RequestBody ServerConfigInfo serverConfigInfo, HttpServletRequest httpServletRequest) {
        try {
            CheckUtil.check(loginService.isLogin(httpServletRequest), "登录不合法, 拒绝访问", HttpStatus.FORBIDDEN.value());
            configService.proceedingAddConfig(serverConfigInfo);
            return new CommonWebResponse<>("添加成功", HttpStatus.OK.value()).toString();
        } catch (CommonException e) {
            return new CommonWebResponse<>(e.getHttpCode(), e.getMessage()).toString();
        } catch (Exception e) {
            return new CommonWebResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString();
        }
    }

    @PostMapping("/saveConfigs")
    public String saveConfigs(@RequestBody List<ServerConfigInfo> serverConfigInfos, HttpServletRequest httpServletRequest) {
        try {
            CheckUtil.check(loginService.isLogin(httpServletRequest), "登录不合法, 拒绝访问", HttpStatus.FORBIDDEN.value());
            configService.proceedingSaveConfig(serverConfigInfos);
            return new CommonWebResponse<>("添加成功", HttpStatus.OK.value()).toString();
        } catch (CommonException e) {
            return new CommonWebResponse<>(e.getHttpCode(), e.getMessage()).toString();
        } catch (Exception e) {
            return new CommonWebResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString();
        }
    }
}
