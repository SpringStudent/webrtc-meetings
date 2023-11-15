package io.springstudent.meeting.common.service;

import io.springstudent.meeting.common.bean.LoginResult;


public interface LoginService {

    LoginResult login(String username, String password) throws Exception;

    void logout()throws Exception;
}
