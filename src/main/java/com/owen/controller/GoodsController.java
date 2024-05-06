package com.owen.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2024/5/6
 * 创建人:Owen
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
	@GetMapping("/delete")
	public String delete() {
		return "Deleted!!!";
	}

	/**
	 * @RequiresRoles生效是因为shiro-spring-boot-starter包中spring.factories配置文件中的 配置类ShiroAnnotationProcessorAutoConfiguration自动装配了DefaultAdvisorAutoProxyCreator
	 * 和AuthorizationAttributeSourceAdvisor的缘故，对Controller进行了CGLIB动态代理（Controller没有父类所以肯定是CGLIB进行的代理）
	 * 注意：注解的形式无法将错误页面的信息定位到401.html,因为配置的这种路径只针对过滤器链有效，注解无效，可以通过@RestControllerAdvice
	 * 实现友好提示效果
	 */
	@GetMapping("/update")
	@RequiresRoles(value = {"Administer", "Marketing Manager"}, logical = Logical.OR)
	public String update() {
		return "Updated...";
	}
}
