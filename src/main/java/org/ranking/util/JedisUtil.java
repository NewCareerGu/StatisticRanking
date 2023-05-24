package org.ranking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
工具类
加载配置文件
 */
public class JedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
    private static JedisPool jedisPool;

    static {
        String host = PropUtil.getProperty("host");
        AssertionUtil.notEmpty(host, "redis host is empty");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(PropUtil.getProperty("maxTotal", "20")));
        config.setMaxIdle(Integer.parseInt(PropUtil.getProperty("maxIdle", "10")));
        jedisPool = new JedisPool(config, host, Integer.parseInt(PropUtil.getProperty("port", "6379")));
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}

