package io.springstudent.meeting.account.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseAccount {
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("真实名称")
    private String realname;
    @ApiModelProperty("密码")
    private String password;
}
