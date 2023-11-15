package io.springstudent.meeting.common.bean;

import lombok.Data;

@Data
public class LoginResult {

    private String username;

    private String realname;

    private String token;
}

