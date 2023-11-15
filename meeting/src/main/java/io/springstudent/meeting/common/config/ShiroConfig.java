package io.springstudent.meeting.common.config;

import io.springstudent.meeting.common.shiro.HeaderWebSessionManager;
import io.springstudent.meeting.common.http.CorsAndXssFilter;
import io.springstudent.meeting.common.http.UserAuthenticationFilter;
import io.springstudent.meeting.common.shiro.UserRealm;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
public class ShiroConfig {


    @Resource
    private RedisManager redisManager;

    /**
     * 自定义认证规则
     */
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        redisSessionDAO.setKeyPrefix(redisSessionDAO.getKeyPrefix() + "business:");
        return redisSessionDAO;
    }

    /**
     * 自定义session管理器，解决前后端分离跨域和app无法携带cookie的问题
     */
    @Bean
    public SessionManager sessionManager() {
        HeaderWebSessionManager headerWebSessionManager = new HeaderWebSessionManager();
        headerWebSessionManager.setSessionIdUrlRewritingEnabled(false);
        headerWebSessionManager.setSessionDAO(redisSessionDAO());
        return headerWebSessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setCacheManager(cacheManager());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }


    /**
     * 授权认证uri配置，认证规则如下
     * anon: 无需认证就可以访问
     * authc: 必须认证才能访问
     * user: 必须拥有记住我功能才能用
     * perms: 拥有对某个资源的权限才能访问
     * role: 拥有某个角色权限才能访问
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filters = new HashMap<>();
        filters.put("authc", new UserAuthenticationFilter());
        factoryBean.setFilters(filters);
        factoryBean.setSecurityManager(securityManager);
        Map<String, String> map = new HashMap<>();
        //允许未登录访问
        map.put("/login", "anon");
        map.put("/logout", "anon");
        map.put("/account/**", "anon");
        map.put("/v3/api-docs", "anon");
        map.put("/swagger-resources/**", "anon");
        map.put("/swagger-ui/**", "anon");
        //必须登录后才可访问
        map.put("/**", "authc");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }

    /**
     * 开启Shiro注解模式，可通过@RequiresRoles注解实现接口调用权限校验
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public FilterRegistrationBean corsAndXssFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new CorsAndXssFilter());
        List<String> urls = new ArrayList<>();
        urls.add("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }


}
