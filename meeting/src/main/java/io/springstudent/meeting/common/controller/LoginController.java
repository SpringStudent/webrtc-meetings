package io.springstudent.meeting.common.controller;

import io.springstudent.meeting.common.shiro.UserPrincipal;
import io.springstudent.meeting.common.util.SubjectUtils;
import io.springstudent.meeting.common.bean.LoginResult;
import io.swagger.annotations.ApiOperation;
import io.springstudent.meeting.common.service.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @Resource
    private LoginService loginService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录", notes = "登录")
    public LoginResult login(@RequestParam String username, @RequestParam String password) throws Exception {
        try {
            return loginService.login(username, password);
        } catch (Exception e) {
            logger.error("login error,username={},password={}", username, password, e);
            throw e;
        }
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    @ApiOperation(value = "登出", notes = "登出")
    public void logout() throws Exception {
        try {
            loginService.logout();
        } catch (Exception e) {
            logger.error("logout error", e);
            throw e;
        }
    }

    /**
     * 获取当前登录用户
     */
    @PostMapping("/currentUser")
    public UserPrincipal currentUser() {
        return SubjectUtils.loginUser();
    }
}
