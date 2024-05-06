package com.owen.config;

import com.owen.filter.RolesOrAuthorizationFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
	@Value("#{ @environment['shiro.loginUrl'] ?: '/login.jsp' }")
	protected String loginUrl;

	@Value("#{ @environment['shiro.successUrl'] ?: '/' }")
	protected String successUrl;

	@Value("#{ @environment['shiro.unauthorizedUrl'] ?: null }")
	protected String unauthorizedUrl;

	@Bean
	public DefaultWebSecurityManager securityManager(Realm realm) {
		return new DefaultWebSecurityManager(realm);
	}

	@Bean
	public DefaultShiroFilterChainDefinition shiroFilterChainDefinition() {
		Map<String, String> pathDefinitions = new LinkedHashMap<>();
		pathDefinitions.put("/login.html", "anon");
		pathDefinitions.put("/user/**", "anon");
		pathDefinitions.put("/goods/delete", "rolesOr[Administer,Operation Manager]");
		pathDefinitions.put("/**", "authc");
		DefaultShiroFilterChainDefinition filterChainDefinition = new DefaultShiroFilterChainDefinition();
		filterChainDefinition.addPathDefinitions(pathDefinitions);
		return filterChainDefinition;
	}

	@Bean
	protected ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroFilterChainDefinition shiroFilterChainDefinition) {
		ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
		filterFactoryBean.setLoginUrl(loginUrl);
		filterFactoryBean.setSuccessUrl(successUrl);
		filterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
		filterFactoryBean.setSecurityManager(securityManager);

		// 此处是Shiro注册自定义过滤器，注意：一定要new，如果让Spring管理这个过滤器会造成SecurityUtils.getSubject()的情况
		filterFactoryBean.getFilters().put("rolesOr",new RolesOrAuthorizationFilter());
		filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());
		return filterFactoryBean;
	}
}
