package com.jk.data.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by 76204 on 2017/7/10.
 */
public class RedisUtils {
    private static JedisPool jedisPool = null;
    static{
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(10);
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxWaitMillis(10000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, "localhost", 6379);
    }

    /**
     * 在key 对应 list的头部添加字符串元素
     * @param Key
     * @param value
     */
    public static void lpush(String Key, String value) {
        Jedis resource = jedisPool.getResource();
        resource.lpush(Key, value);
        resource.close();
    }

    public static void  set(String  key,String  value){
        Jedis resource = jedisPool.getResource();
        resource.set(key,value);
        resource.close();
    }

    public static String  get(String  key){
        Jedis resource = jedisPool.getResource();
        String s = resource.get(key);
        resource.close();
        return s;
    }


}
