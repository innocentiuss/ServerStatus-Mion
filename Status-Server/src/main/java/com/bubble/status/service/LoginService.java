package com.bubble.status.service;

import com.bubble.status.exceptions.CommonException;
import com.bubble.status.model.CommonWebResponse;
import com.bubble.status.model.Configs;
import com.bubble.status.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import com.bubble.status.model.Login;
import com.bubble.status.utils.CheckUtil;
import com.bubble.status.utils.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
            String cookieVal = sha1(settingLoginInfoMD5.getUsername() + settingLoginInfoMD5.getPassword());
            addCookie(httpServletResponse, "isLogin", cookieVal, 1200, "/");
            return new CommonWebResponse<>(200, "login ok").toString();
        }
        return new CommonWebResponse<>(401, "login failed").toString();
    }

    /**
     * 检查是否登录
     * @param request http请求 用来拿cookie
     * @return json string
     */
    public String checkLogin(HttpServletRequest request) {
        if (isLogin(request))
            return new CommonWebResponse<>(HttpStatus.OK.value(), "ok").toString();

        return new CommonWebResponse<>("/login", HttpStatus.TEMPORARY_REDIRECT.value()).toString();
    }

    /**
     * 从配置文件拿登录信息 并将数据过一次md5
     * @return 登录信息obj
     */
    private Login getMD5LoginConfigInfo() {
        // 从配置文件拿数据
        String jsonString = IOUtil.readJsonConfig(configFileName);
        Configs configs = JsonUtil.toObject(jsonString, Configs.class);
        if (configs == null || configs.getLoginInfo() == null) {
            throw new CommonException("配置文件缺少登录信息.");
        }
        Login login = configs.getLoginInfo();
        String settingNameMD5 = md5(login.getUsername());
        String settingPassMD5 = md5(login.getPassword());
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
        Cookie cookie = getCookie(request, "isLogin");
        return cookie != null &&
                CheckUtil.isSame(
                        sha1(settingLoginInfoMD5.getUsername() + settingLoginInfoMD5.getPassword()),
                        cookie.getValue()
                );
    }

    // 辅助工具方法
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        // 如果需要，可以设置 cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private String digest(String algorithm, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // 转16进制
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String md5(String input) {
        return digest("MD5", input);
    }

    private String sha1(String input) {
        return digest("SHA-1", input);
    }
}
