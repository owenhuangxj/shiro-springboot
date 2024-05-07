package com.owen.config;

import com.owen.cache.RedisCacheManager;
import com.owen.filter.RolesOrAuthorizationFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
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


	/**
	 * Shiro默认的SessionDAO为MemorySessionDAO,自定义RedisSessionDao使用@Primary注解，否则此处多个SessionDAO实例会报错
	 */
	@Bean
	protected SessionManager sessionManager(SessionDAO sessionDao) {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(sessionDao);
		return sessionManager;
	}

	@Bean
	public DefaultWebSecurityManager securityManager(Realm realm, SessionManager sessionManager,
													 RedisCacheManager cacheManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
		securityManager.setSessionManager(sessionManager);
		securityManager.setCacheManager(cacheManager);
		return securityManager;
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

	/**
	 * 参照shiro-spring-boot-web-starter包中的spring.factories文件中的
	 * org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration的
	 * 父类AbstractShiroWebFilterConfiguration对ShiroFilterFactoryBean的处理
	 * 注意：有自定义Filter时需要向Shiro提供ShiroFilterFactoryBean
	 *
	 * @param securityManager            SecurityManager：securityManager方法提供的DefaultWebSecurityManager
	 * @param shiroFilterChainDefinition ShiroFilterChainDefinition：shiroFilterChainDefinition
	 *                                   方法提供的DefaultShiroFilterChainDefinition
	 * @return ShiroFilterFactoryBean
	 */
	@Bean
	protected ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
															ShiroFilterChainDefinition shiroFilterChainDefinition) {
		ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
		filterFactoryBean.setLoginUrl(loginUrl);
		filterFactoryBean.setSuccessUrl(successUrl);
		filterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
		filterFactoryBean.setSecurityManager(securityManager);

		// 此处是Shiro注册自定义过滤器，注意：一定要new，如果让Spring管理这个过滤器会造成SecurityUtils.getSubject()获取到null的情况
		filterFactoryBean.getFilters().put("rolesOr", new RolesOrAuthorizationFilter());
		filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());
		return filterFactoryBean;
	}
}
