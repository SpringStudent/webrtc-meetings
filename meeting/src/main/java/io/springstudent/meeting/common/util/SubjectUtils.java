package io.springstudent.meeting.common.util;

import io.springstudent.meeting.common.shiro.UserPrincipal;
import org.apache.shiro.SecurityUtils;

public class SubjectUtils {

    /**
     * 获取登录用户名
     */
    public static String loginUserName() {
        UserPrincipal userPrincipal = loginUser();
        return userPrincipal != null ? userPrincipal.getUsername() : null;
    }

    /**
     * 登录用户信息
     */
    public static UserPrincipal loginUser() {
        return (UserPrincipal) SecurityUtils.getSubject().getPrincipal();
    }



}
