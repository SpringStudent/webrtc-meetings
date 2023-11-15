package io.springstudent.meeting.common.http;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsAndXssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //跨域处理
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "content-type,Cookie, Token, Random, Signature, Timestamp, Content-Length, Content-Disposition, filename");
        response.setHeader("Access-Control-Max-Age", "3600");
        if(request.getRequestURI().indexOf("socket.io")!=-1){
            filterChain.doFilter(request, response);
        }else{
            filterChain.doFilter(new XssRequestWrapper(request), response);
        }

    }
}
