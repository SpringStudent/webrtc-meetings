package io.springstudent.meeting.account.controller;

import io.springstudent.meeting.account.bean.BaseAccount;
import io.springstudent.meeting.account.service.AccountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;

/**
 * @author zhouning
 * @date 2023/02/27 17:41
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    private static final Logger logger = LogManager.getLogger(AccountController.class);

    @Resource
    private AccountService accountService;

    @PostMapping("/add")
    @ApiOperation(value = "添加账号", notes = "添加账号")
    public void add(@RequestBody BaseAccount baseAccount) throws Exception {
        try {
            accountService.add(baseAccount);
        } catch (Exception e) {
            logger.error("add error,baseAccount={}", baseAccount, e);
            throw e;
        }
    }
}
