package io.springstudent.meeting.common.service.impl;

import io.springstudent.meeting.common.bean.LoginResult;
import io.springstudent.meeting.common.service.LoginService;
import io.springstudent.meeting.common.shiro.UserPrincipal;
import io.springstudent.meeting.common.util.SubjectUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;


@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private SessionDAO sessionDAO;

    @Override
    public LoginResult login(String username, String password) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        subject.login(usernamePasswordToken);
        String token = String.valueOf(subject.getSession().getId());
        UserPrincipal userPrincipal = SubjectUtils.loginUser();
        //同一用户多次登录互踢
        kickSameUser(username, token);
        LoginResult loginResult = new LoginResult();
        loginResult.setUsername(userPrincipal.getUsername());
        loginResult.setRealname(userPrincipal.getRealname());
        loginResult.setToken(token);
        return loginResult;
    }

    private void kickSameUser(String loginUser, String token) throws Exception {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            if (session != null && session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) != null) {
                SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
                        .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                UserPrincipal userPrincipal = (UserPrincipal) principalCollection.getPrimaryPrincipal();
                //同一个用户已经登录过一次,直接踢出
                if (userPrincipal.getUsername().equals(loginUser) && !token.equals(String.valueOf(session.getId()))) {
                    sessionDAO.delete(session);
                    if (session != null) {
                        session.stop();
                    }
                }
            }
        }
    }

    @Override
    public void logout() throws Exception {
        if (SubjectUtils.loginUser() != null) {
            SecurityUtils.getSubject().logout();
        }
    }
}
