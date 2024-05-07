package com.owen.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;

/**
 * 装饰器模式重写实现retrieveSession方法，实现每次请求从ServletRequest中查询Session信息避免一次请求多次请求redis
 */
@Slf4j
@Component
public class RedisDefaultWebSessionManager extends DefaultWebSessionManager {
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        // 通过sessionKey获取sessionId
        String sessionId = ((String) getSessionId(sessionKey));

        // 将sessionKey转换为WebSessionKey
        if (sessionKey instanceof WebSessionKey) {
            WebSessionKey webSessionKey = (WebSessionKey) sessionKey;
            ServletRequest servletRequest = webSessionKey.getServletRequest();
            Session session = (Session) servletRequest.getAttribute(sessionId);
            if (session != null) {
                log.info("Retrieved session info from ServletRequest!!!");
            } else {
                session = retrieveSessionFromDataSource(sessionId);
                if (session == null) {
                    //session ID was provided, meaning one is expected to be found, but we couldn't find one:
                    throw new UnknownSessionException("Could not find session with ID [" + sessionId + "]");
                }
                log.info("Retrieved session info from datasource!!!");
                servletRequest.setAttribute(sessionId, session);
            }
            return session;
        }
        return null;
    }
}
