package io.springstudent.meeting.account.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private String id;

    private String pwd;

    private String realname;

    private String username;

    private String salt;


}
