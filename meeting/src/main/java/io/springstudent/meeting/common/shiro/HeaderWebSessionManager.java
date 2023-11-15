package io.springstudent.meeting.common.shiro;

import cn.hutool.core.util.StrUtil;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

public class HeaderWebSessionManager extends DefaultWebSessionManager implements ShiroVariable {


    public HeaderWebSessionManager() {
        super();
        setGlobalSessionTimeout(SESSION_TIMEOUT * 1000);
    }

    /**
     * 解决shiro的session跨域共享和app端无法携带cookie的问题
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //首先从http请求头Token中获取sessionId，获取不到在从cookie获取
        String sessionId = WebUtils.toHttp(request).getHeader(TOKEN);
        if (StrUtil.isEmpty(sessionId)) {
            return super.getSessionId(request, response);
        } else {
            //防止shiro重定向在url中拼接jsessionId字符串，导致请求400
            request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, this.isSessionIdUrlRewritingEnabled());
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "Stateless");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;
        }
    }
}
