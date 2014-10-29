package com.iwaki.web.service;

import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.cache.RedisManager;
import com.iwaki.web.model.ScoreRank;

@Service
public class GameServiceImpl implements GameService {

	private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
	
	@Autowired
	private RedisManager redisManager;
	
	@Value("${game.rank.max}")
	private int rankMax;

	@Override
	public void incrSharedCount(String openid, String gamerIp) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			jedis.sadd(sharedKey(openid), gamerIp);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}
	
	public long incrRank(ScoreRank scoreRank) {
		Jedis jedis = null;
		Long rank = -1L;
		try { 
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(scoreRank);
			jedis = redisManager.getRedisInstance();
			String rankKey = rankKey();
			String openid = scoreRank.getOpenid();
			Double oldScore = jedis.zscore(rankKey, openid);
			if (oldScore == null || scoreRank.getScore() > oldScore) {
				jedis.zadd(rankKey, scoreRank.getScore(), openid);
				rank = jedis.zrevrank(rankKey, openid);
			} else { // 临时排名
				jedis.zadd(rankKey, scoreRank.getScore(), "temp");
				rank = jedis.zrevrank(rankKey, "temp");
				jedis.zrem(rankKey, "temp");
			}
			jedis.set(playerKey(openid), json);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return rank + 1;
	}
	
	public ArrayList<ScoreRank> topRanks() {
		Jedis jedis = null;
		ArrayList<ScoreRank> ranks = new ArrayList<ScoreRank>();
		try { 
			jedis = redisManager.getRedisInstance();
			String key = rankKey();
			Set<String> playerKeys = jedis.zrevrange(key, 0, rankMax);
			ObjectMapper mapper = new ObjectMapper();
			for (String pKey : playerKeys) {
				String json = jedis.get(playerKey(pKey));
				ScoreRank rank = mapper.readValue(json, ScoreRank.class);
				ranks.add(rank);
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return ranks;
	}

	@Override
	public void acceptArticle(String openid) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = articleKey();
			jedis.sadd(key, openid);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}		
	}

	@Override
	public boolean hasAcceptArticle(String openid) {
		Jedis jedis = null;
		boolean has = false;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = articleKey();
			has = jedis.sismember(key, openid);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return has;
	}
	
	//============================================
	private String sharedKey(String openid) {
		return "shared:" + openid;
	}
		
	private String rankKey() {
		return "rank:";
	}
		
	private String playerKey(String openid) {
		return "player:" + openid;
	}
	
	private String articleKey() {
		return "article:";
	}

}
