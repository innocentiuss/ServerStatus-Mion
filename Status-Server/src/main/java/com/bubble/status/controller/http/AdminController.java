package com.bubble.status.controller.http;

import cn.hutool.http.HttpStatus;
import com.bubble.status.exceptions.CommonException;
import com.bubble.status.exceptions.ConfigErrorException;
import com.bubble.status.model.ConfigInfo;
import com.bubble.status.model.Login;
import com.bubble.status.model.WebResponse;
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
            return new WebResponse(e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR).toString();
        }
    }

    @PostMapping("/checkLogin")
    public String checkLogin(HttpServletRequest httpServletRequest) {
        try {
            return loginService.checkLogin(httpServletRequest);
        } catch (Exception e) {
            return new WebResponse(e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR).toString();
        }
    }

    @GetMapping("/getConfigs")
    public String getConfigs() {
        try {
            return new WebResponse(configService.getAllConfigs(), HttpStatus.HTTP_OK).toString();
        } catch (Exception e) {
            return new WebResponse(e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR).toString();
        }
    }

    @GetMapping("/reloadConfigs")
    public String reloadConfigs() {
        try {
            configService.refreshConfig();
        } catch (ConfigErrorException | IOException exception) {
            return new WebResponse(exception.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR).toString();
        }
        return new WebResponse("ok", HttpStatus.HTTP_OK).toString();
    }

    @PostMapping("/addConfig")
    public String addConfigs(@RequestBody ConfigInfo configInfo, HttpServletRequest httpServletRequest) {
        try {
            CheckUtil.check(loginService.isLogin(httpServletRequest), "登录不合法, 拒绝访问", HttpStatus.HTTP_FORBIDDEN);
            configService.proceedingAddConfig(configInfo);
            return new WebResponse("添加成功", HttpStatus.HTTP_OK).toString();
        } catch (CommonException e) {
            return new WebResponse(e.getMessage(), e.getHttpCode()).toString();
        } catch (Exception e) {
            return new WebResponse(e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR).toString();
        }
    }

    @PostMapping("/saveConfigs")
    public String saveConfigs(@RequestBody List<ConfigInfo> configInfos, HttpServletRequest httpServletRequest) {
        try {
            CheckUtil.check(loginService.isLogin(httpServletRequest), "登录不合法, 拒绝访问", HttpStatus.HTTP_FORBIDDEN);
            configService.proceedingSaveConfig(configInfos);
            return new WebResponse("添加成功", HttpStatus.HTTP_OK).toString();
        } catch (CommonException e) {
            return new WebResponse(e.getMessage(), e.getHttpCode()).toString();
        } catch (Exception e) {
            return new WebResponse(e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR).toString();
        }
    }
}
