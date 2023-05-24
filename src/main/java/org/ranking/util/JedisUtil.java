package org.ranking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis工具雷
 *
 * @author Gu
 * @since 2023-05-24
 */
public class JedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
    private static JedisPool jedisPool;

    static {
        String host = PropUtil.getProperty("redis.host","127.0.0.1");
        AssertionUtil.notEmpty(host, "redis host is empty");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(PropUtil.getProperty("jedis.pool.maxTotal", "20")));
        config.setMaxIdle(Integer.parseInt(PropUtil.getProperty("jedis.pool.maxIdle", "10")));
        jedisPool = new JedisPool(config, host, Integer.parseInt(PropUtil.getProperty("redis.port", "6379")));
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}

