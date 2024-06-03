package com.owen.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;

/**
 * 装饰器模式重写DefaultWebSessionManager实现retrieveSession方法，
 * 实现每次请求从ServletRequest中查询Session信息避免一次WEB请求多次从RedisSessionDao中请求Redis来获取Session
 */
@Slf4j
public class RedisDefaultWebSessionManager extends DefaultWebSessionManager {
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        // 通过sessionKey获取sessionId
        String sessionId = ((String) getSessionId(sessionKey));

        // 避免在访问http://localhost:8080/时因为sessionId为null造成(Session) servletRequest.getAttribute(sessionId)空指针
        if (sessionId == null) {
            log.info("Parameter sessionId is null>>>");
            return null;
        }

        if (!(sessionKey instanceof WebSessionKey)) {
            return null;
        }

        // 将sessionKey转换为WebSessionKey
        WebSessionKey webSessionKey = (WebSessionKey) sessionKey;
        ServletRequest servletRequest = webSessionKey.getServletRequest();

        // 此处的session是通过servletRequest.setAttribute(sessionId, session)放进去的，先从ServletRequest域获取避免
        // 从SessionDAO(工程中是RedisSessionDao)中获取，减少Redis的压力
        Session session = (Session) servletRequest.getAttribute(sessionId);
        if (session != null) {
            log.info("Retrieved session info from ServletRequest!!!");
        } else {
            // 实际就是通过SessionDAO获取Session(SessionDAO.readSession最终是通过doReadSession方法获取)
            session = retrieveSessionFromDataSource(sessionId);
            if (session == null) {
                //session ID was provided, meaning one is expected to be found, but we couldn't find one:
                throw new UnknownSessionException("Could not find session with ID [" + sessionId + "]");
            }
            log.info("Retrieved session info from datasource>>>");
            // 将retrieveSessionFromDataSource获取的Session存入ServletRequest域
            servletRequest.setAttribute(sessionId, session);
        }
        return session;
    }
}
