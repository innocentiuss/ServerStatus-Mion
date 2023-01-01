package com.bubble.status.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bubble.status.model.Login;
import com.bubble.status.model.WebResponse;
import com.bubble.status.utils.CheckUtil;
import com.bubble.status.utils.ReadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginService {

    @Value("${server.config}")
    private String configFileName;

    public String doLogin(Login loginInfo, HttpServletResponse httpServletResponse) {
        Login settingLoginInfoMD5 = getMD5LoginConfigInfo();

        // check并下发cookie
        if (CheckUtil.isSame(settingLoginInfoMD5.getUsername(), loginInfo.getUsername())
                && CheckUtil.isSame(settingLoginInfoMD5.getPassword(), loginInfo.getPassword())) {

            // 计算cookie值, 暂时定义为用户名+密码过完MD5后过一次sha1
            String cookieVal = SecureUtil.sha1(settingLoginInfoMD5.getUsername() + settingLoginInfoMD5.getPassword());
            ServletUtil.addCookie(httpServletResponse, "isLogin", cookieVal, 600, "/", null);
            return new WebResponse("login ok", 200).toString();
        }
        return new WebResponse("login failed", 401).toString();
    }

    public String checkLogin(HttpServletRequest request) {
        Login settingLoginInfoMD5 = getMD5LoginConfigInfo();
        Cookie cookie = ServletUtil.getCookie(request, "isLogin");

        // 检查cookie是否正确
        if (cookie != null &&
                CheckUtil.isSame(SecureUtil.sha1(settingLoginInfoMD5.getUsername() + settingLoginInfoMD5.getPassword())
                        , cookie.getValue())) {
            return new WebResponse("ok", 200).toString();
        }

        return new WebResponse("/login", 301).toString();
    }

    private Login getMD5LoginConfigInfo() {
        // 从配置文件拿数据
        String jsonString = ReadUtil.readJsonConfig(configFileName);
        CheckUtil.check(jsonString != null, "read config error!");
        JSONObject loginInfoSet = JSON.parseObject(jsonString).getJSONObject("loginInfo");
        String settingNameMD5 = SecureUtil.md5(loginInfoSet.getString("username"));
        String settingPassMD5 = SecureUtil.md5(loginInfoSet.getString("password"));
        return new Login(settingNameMD5, settingPassMD5);
    }
}
