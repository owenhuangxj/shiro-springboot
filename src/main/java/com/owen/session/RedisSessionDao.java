package com.owen.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * com.owen.config.ShiroConfig#sessionManager中注入了此类实例
 */
@Slf4j
@Primary
@Component
public class RedisSessionDao extends AbstractSessionDAO {
    private final static String SESSION_PREFIX = "ShiroSession:";

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            return null;
        }
        log.info("Method doCreate>>>");

        // 基于Session生成一个sessionId
        String sessionId = SESSION_PREFIX + generateSessionId(session);

        // 将Session和sessionId绑定到一起（可以基于Session拿到sessionId）
        assignSessionId(session,sessionId);
        redisTemplate.opsForValue().set(sessionId, session, 1, TimeUnit.HOURS);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        log.info("Read session from redis>>>");
        Session session = (Session) redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
        if (session != null) {
            redisTemplate.opsForValue().set(SESSION_PREFIX + sessionId, session, 1, TimeUnit.HOURS);
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            return;
        }
        log.info("Update session in redis>>>");
        String sessionId = SESSION_PREFIX + session.getId();
        redisTemplate.opsForValue().set(sessionId, session, 1, TimeUnit.HOURS);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            return;
        }
        log.info("Delete session in redis>>>");
        redisTemplate.delete(SESSION_PREFIX + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        log.info("Get active sessions from redis>>>");
        Set keys = redisTemplate.keys(SESSION_PREFIX + "*");
        Set<Session> sessions = new HashSet<>();
        for (Object key : keys) {
            Session session = (Session) redisTemplate.opsForValue().get(key);
            sessions.add(session);
        }
        return sessions;
    }
}
