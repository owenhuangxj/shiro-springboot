package com.owen.realm;

import com.alibaba.druid.util.StringUtils;
import com.owen.entity.Permission;
import com.owen.entity.Role;
import com.owen.entity.User;
import com.owen.service.PermissionService;
import com.owen.service.RoleService;
import com.owen.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Component
@Primary
@Slf4j
public class ShiroRealm extends AuthorizingRealm {
//    {
//        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
//        matcher.setHashAlgorithmName("MD5");
//        matcher.setHashIterations(1024);
//        this.setCredentialsMatcher(matcher);
//    }

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 认证方法,方法中只需要通过username而不是通过username和password获取用户信息是因为AuthenticatingRealm#assertCredentialsMatch()
     * 方法在做密码的比较
     *
     * @param token 认证token,是用户在身份验证尝试期间提交的帐户主体和支持凭据的整合。
     * @return AuthenticationInfo:表示Subject(即用户)存储的仅与身份验证/登录过程相关的帐户信息。
     * @throws AuthenticationException 认证异常
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        // 返回nullShiro框架会报错AuthenticationException
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        User dbUser = userService.findUserByName(username);
        if (dbUser == null) {
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(dbUser, dbUser.getPassword(),
                "ShiroRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(dbUser.getSalt()));
        return authenticationInfo;
    }

    /**
     * 用户授权:此方法应该通过principals对象中保存的User数据最终获取到角色和权限数据然后封装成SimpleAuthorizationInfo返回
     * 方法返回后会在父类方法AuthorizingRealm#getAuthorizationInfo(PrincipalCollection)中进行缓存；
     * 而缓存逻辑是这样的：首先获取Cache(Cache是通过AuthorizingRealm#getAuthorizationCacheLazy()获取的，具体逻辑在
     * AuthorizingRealm#getAuthorizationCacheLazy()中，具体是先获取CacheManager，然后通过CacheManager.getCache(cacheName)
     * 获取Cache，而cacheName是在工程启动时或者准确得说是在创建Realm(本工程是ShiroRealm)时通过调用Realm的无参构造构造的，
     * cacheName构造具体步骤ShiroRealm无参构造->AuthorizingRealm无参构造->AuthorizingRealm(CacheManager CredentialsMatcher)
     * ->this.authorizationCacheName = getClass().getName() + DEFAULT_AUTHORIZATION_CACHE_SUFFIX;此处生成的cacheName，从此
     * 行代码来看实际Cache是和Realm的全限定名绑定到一起的
     *
     * )
     *
     * @param principals 与相应Subject相关联的所有主体的集合，此方法的AuthorizationInfo类型返回对象是通过此对象保存的用户信息
     *                   关联的角色和权限数据组成
     *                   主体只是标识属性的安全术语，例如用户名或用户id或社会保险号或任何其他内容都可以被视为Subject的“标识”属性。
     * @return AuthorizationInfo：表示仅在授权(访问控制)检查期间使用的单个Subject存储的授权数据(角色、权限等)。
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("Retrieve authorization info from datasource!!!");
        // SimpleAuthenticationInfo(java.lang.Object, java.lang.Object, java.lang.String)构造方法保存，
        // 认证<code>this.doGetAuthenticationInfo</code>方法内部逻辑：用户通过认证后就应该将用户信息存入Shiro的
        // SimpleAuthenticationInfo对象中，而SimpleAuthenticationInfo对象中principals属性是PrincipalCollection类型(即此方法
        // 的入参)，该类型是iterator的子类，本工程保存的是User对象，所以此方法获取时强转为User
        User user = (User) principals.getPrimaryPrincipal();
        Set<Role> roles = roleService.findRolesByUserId(user.getId());
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        Set<Integer> roleIds = new HashSet<>();
        Set<String> roleNames = new HashSet<>();
        for (Role role : roles) {
            roleIds.add(role.getId());
            roleNames.add(role.getName());
        }
        Set<Permission> permissions = permissionService.findPermissionByRoleIds(roleIds);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo(roleNames);
        authorizationInfo.setStringPermissions(permissions.stream().map(Permission::getName).collect(Collectors.toSet()));
        return authorizationInfo;
    }
}
