package com.iwaki.web.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisManager {

	@Value("${redis.host}")
	private String host;

	@Value("${redis.port}")
	private int port;
	
	@Value("${redis.password}")
	private String password;

	private JedisPool pool = null;

	public JedisPool getPool() {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setTestOnBorrow(true);
			pool = new JedisPool(config, host, port, 900000, password);
			//pool = new JedisPool(host, port);
		}
		return pool;
	}

	public void returnResource(Jedis redis) {
		if (redis != null) {
			pool.returnResource(redis);
		}
	}
	
	public Jedis getRedisInstance() {
		Jedis jedis = this.getPool().getResource();
		return jedis;
	}

}
