package io.springstudent.meeting.common.shiro;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPrincipal implements Serializable {

    private String username;

    private String realname;
}
