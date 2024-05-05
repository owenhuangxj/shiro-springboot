package com.owen.config;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public DefaultWebSecurityManager securityManager(Realm realm) {
        return new DefaultWebSecurityManager(realm);
    }

    @Bean
    public DefaultShiroFilterChainDefinition shiroFilterChainDefinition() {
        Map<String, String> pathDefinitions = new LinkedHashMap<>();
        pathDefinitions.put("/login.html", "anon");
        pathDefinitions.put("/user/**", "anon");
        pathDefinitions.put("/**", "authc");
        DefaultShiroFilterChainDefinition filterChainDefinition = new DefaultShiroFilterChainDefinition();
        filterChainDefinition.addPathDefinitions(pathDefinitions);
        return filterChainDefinition;
    }
}
