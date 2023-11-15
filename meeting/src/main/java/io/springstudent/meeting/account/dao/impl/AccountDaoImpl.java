package io.springstudent.meeting.account.dao.impl;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.springstudent.meeting.account.dao.AccountDao;
import io.springstudent.meeting.account.pojo.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AccountDaoImpl extends EntityDaoImpl<Account,Integer>implements AccountDao {


}
