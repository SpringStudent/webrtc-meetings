package io.springstudent.meeting.account.service.impl;

import cn.hutool.core.util.IdUtil;
import com.gysoft.jdbc.bean.Criteria;
import io.springstudent.meeting.account.bean.BaseAccount;
import io.springstudent.meeting.account.dao.AccountDao;
import io.springstudent.meeting.account.pojo.Account;
import io.springstudent.meeting.account.service.AccountService;
import io.springstudent.meeting.common.exception.ParamInvalidException;
import io.springstudent.meeting.common.util.SubjectUtils;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Override
    public Account findByUsername(String username) throws Exception {
        return accountDao.queryOne(new Criteria().where("username",username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(BaseAccount baseAccount) throws Exception {
        String salt = IdUtil.fastSimpleUUID().substring(0, 6);
        Md5Hash md5Hash = new Md5Hash(baseAccount.getPassword(), salt, 1024);
        accountDao.save(Account.builder().username(baseAccount.getUsername()).realname(baseAccount.getRealname())
                .pwd(md5Hash.toHex()).salt(salt).build());
    }

}
