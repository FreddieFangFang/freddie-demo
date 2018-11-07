package com.weimob.saas.ec.limitation.utils;

import com.alibaba.fastjson.JSON;
import com.weimob.redis.enhance.client.JedisEnhanceClient;
import com.weimob.redis.enhance.config.WmJedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import java.math.BigDecimal;
import java.util.List;

public class LimitationRedisClientUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(LimitationRedisClientUtils.class);

    public static JedisEnhanceClient jedisEnhanceClient = (JedisEnhanceClient) SpringBeanUtils.getBean("limitationRedisEnchanceClient");

    private static WmJedisPool discountWmJedisPool = (WmJedisPool) SpringBeanUtils.getBean("limitationWmJedisPool");

	 /**
     * redis队列压入值
     * 
     * @param key
     * @param value
     */
    public static void pushDataToQueue(String key,String value){
        Jedis jedis = null;
        try{
            jedis = discountWmJedisPool.getResource();
            jedis.rpush(key, value);
        }catch (Exception e){
            LOGGER.error("Push data to queen error, key:" + key + ", value:" + value, e);
        }finally{
            if (jedis != null){
                jedis.close();
            }
        }
    }
	/**
	 * redis队列取出值
	 *
	 * @param key
	 * @param pageSize
	 */
    public static List<Object> popDataFromQueen(String key,long pageSize){
        List<Object> objectlist = null;
        Jedis jedis = null;
        try{
            jedis = discountWmJedisPool.getResource();
            long llen = jedis.llen(key);
            long length = (llen > pageSize) ? pageSize : llen;
            Pipeline pipelinePop = jedis.pipelined();
            for (int i = 0; i < length; i++){
                pipelinePop.lpop(key);
            }
            objectlist = pipelinePop.syncAndReturnAll();
        }catch (Exception e){
            LOGGER.error("Pop data from queen error, key:" + key, e);
        }finally{
            if (jedis != null){
                jedis.close();
            }
        }
        return objectlist;
    }
}
