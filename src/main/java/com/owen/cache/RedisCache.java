package com.owen.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisCache避免分布式系统出现请求会要求频繁登陆的场景：登陆信息默认是保存到JVM中的
 * TODO:目前只是实现功能,Redis操作考虑其他Redis API减少循环IO操作
 */
@Slf4j
@Component
public class RedisCache<K, V> implements Cache<K, V> {

    @Autowired
    private RedisTemplate redisTemplate;

    private final String SHIRO_CACHE_PREFIX = "ShiroCache:";

    /**
     * 获取K对应的缓存
     *
     * @param k key
     * @return 缓存信息
     * @throws CacheException 缓存异常
     */
    @Override
    public V get(K k) throws CacheException {
        log.info("Retrieve user authorization info from redis!!!");
        if (k == null) {
            return null;
        }
        V v = (V) redisTemplate.opsForValue().get(SHIRO_CACHE_PREFIX + k);
        if (v != null) {
            redisTemplate.opsForValue().set(SHIRO_CACHE_PREFIX + k, v, 1, TimeUnit.HOURS);
        }
        return v;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForValue().set(SHIRO_CACHE_PREFIX + k, v, 1, TimeUnit.HOURS);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V v = (V) redisTemplate.opsForValue().get(SHIRO_CACHE_PREFIX + k);
        if (v != null) {
            redisTemplate.delete(SHIRO_CACHE_PREFIX + k);
        }
        return v;
    }

    /**
     * 清空全部缓存信息
     *
     * @throws CacheException
     */
    @Override
    public void clear() throws CacheException {
        Set removingKeys = redisTemplate.keys(SHIRO_CACHE_PREFIX + "*");
        redisTemplate.delete(removingKeys);
    }

    @Override
    public int size() {
        return CollectionUtils.size(keys());
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.keys(SHIRO_CACHE_PREFIX + "*");
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = redisTemplate.keys(SHIRO_CACHE_PREFIX + "*");
        Set<V> values = new HashSet<>();
        for (Object key : keys) {
            values.add(((V) redisTemplate.opsForValue().get(key)));
        }
        return values;
    }
}
