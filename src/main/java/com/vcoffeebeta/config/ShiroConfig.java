package com.vcoffeebeta.config;

import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * spring没有提供shiro的配置，增加对shiro的配置类
 * @author zhangshenming
 * @date 2022/10/9 20:26
 * @version 1.0
 */
@Configuration
public class ShiroConfig {

    @Bean
    public IniRealm getIniRealm(){
        IniRealm iniRealm = new IniRealm("classpath:shiro.ini");
        return iniRealm;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //securityManager需要完成校验，需要realm
        defaultWebSecurityManager.setRealm(getIniRealm());
        return defaultWebSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(getDefaultWebSecurityManager());
        // 设置shiro拦截规则：
        // anon：匿名用户可访问 authc：认证用户可访问
        // user：使用remember me的用户可以访问 permission：对应权限可以访问
        // role：对应的角色可以访问
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("/","anon");
        filterMap.put("/login/login.html","anon");
        filterMap.put("/registry/registry.html","anon");
        filterMap.put("/company/*.html","authc");
        filterMap.put("/user/*.html","authc");
        filterMap.put("/equipment/*.html","authc");
        filterMap.put("/consume/*.html","authc");
        filterMap.put("/account/*.html","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setLoginUrl("/login/login.html");
        //设置未授权页面跳转到登录页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/login/login.html");
        return shiroFilterFactoryBean;
    }
}
