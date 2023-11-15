package io.springstudent.meeting.account.service;

import io.springstudent.meeting.account.bean.BaseAccount;
import io.springstudent.meeting.account.pojo.Account;

public interface AccountService {

    Account findByUsername(String username) throws Exception;

    void add(BaseAccount baseAccount) throws Exception;

}
