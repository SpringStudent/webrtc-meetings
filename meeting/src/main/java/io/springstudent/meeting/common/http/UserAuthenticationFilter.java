package io.springstudent.meeting.common.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UserAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger logger = LogManager.getLogger(UserAuthenticationFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        } else {
            saveRequest(request);
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
            resp.setHeader("Access-Control-Allow-Methods", "*");
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Access-Control-Allow-Headers", "content-type,Cookie, Token, Random, Signature, Timestamp, Content-Length, Content-Disposition, filename");
            resp.setHeader("Access-Control-Max-Age", "3600");
            resp.setContentType("application/json; charset=utf-8");
            Object result = null;
            resp.getWriter().write("{\"msg\": \"用户未登录\",\"code\": " + HttpStatus.UNAUTHORIZED.value() + ",\"result\": " + result + "}");
            return false;
        }
    }

}