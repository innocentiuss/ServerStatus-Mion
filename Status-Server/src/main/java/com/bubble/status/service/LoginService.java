package com.bubble.status.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bubble.status.model.Login;
import com.bubble.status.model.WebResponse;
import com.bubble.status.utils.CheckUtil;
import com.bubble.status.utils.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginService {

    @Value("${server.config}")
    private String configFileName;

    /**
     * 后台登录
     * @param loginInfo 传入登录信息 已经在前端过了md5
     * @param httpServletResponse 用于下发cookie
     * @return json string
     */
    public String doLogin(Login loginInfo, HttpServletResponse httpServletResponse) {
        Login settingLoginInfoMD5 = getMD5LoginConfigInfo();

        // check并下发cookie
        if (CheckUtil.isSame(settingLoginInfoMD5.getUsername(), loginInfo.getUsername())
                && CheckUtil.isSame(settingLoginInfoMD5.getPassword(), loginInfo.getPassword())) {

            // 计算cookie值, 暂时定义为用户名+密码过完MD5后过一次sha1
            String cookieVal = SecureUtil.sha1(settingLoginInfoMD5.getUsername() + settingLoginInfoMD5.getPassword());
            ServletUtil.addCookie(httpServletResponse, "isLogin", cookieVal, -1, "/", null);
            return new WebResponse("login ok", 200).toString();
        }
        return new WebResponse("login failed", 401).toString();
    }

    /**
     * 检查是否登录
     * @param request http请求 用来拿cookie
     * @return json string
     */
    public String checkLogin(HttpServletRequest request) {
        if (isLogin(request))
            return new WebResponse("ok", HttpStatus.HTTP_OK).toString();

        return new WebResponse("/login", HttpStatus.HTTP_TEMP_REDIRECT).toString();
    }

    /**
     * 从配置文件拿登录信息 并将数据过一次md5
     * @return 登录信息obj
     */
    private Login getMD5LoginConfigInfo() {
        // 从配置文件拿数据
        String jsonString = IOUtil.readJsonConfig(configFileName);
        CheckUtil.check(jsonString != null, "read config error!", HttpStatus.HTTP_INTERNAL_ERROR);
        JSONObject loginInfoSet = JSON.parseObject(jsonString).getJSONObject("loginInfo");
        String settingNameMD5 = SecureUtil.md5(loginInfoSet.getString("username"));
        String settingPassMD5 = SecureUtil.md5(loginInfoSet.getString("password"));
        return new Login(settingNameMD5, settingPassMD5);
    }

    /**
     * 主要是用于校验cookie
     * @param request http请求 用来拿cookie
     * @return true: 登录合法; false: 登录不合法
     */
    public boolean isLogin(HttpServletRequest request) {
        // 检查cookie是否正确
        Login settingLoginInfoMD5 = getMD5LoginConfigInfo();
        Cookie cookie = ServletUtil.getCookie(request, "isLogin");
        return cookie != null &&
                CheckUtil.isSame(
                        SecureUtil.sha1(settingLoginInfoMD5.getUsername() + settingLoginInfoMD5.getPassword()),
                        cookie.getValue()
                );
    }
}
