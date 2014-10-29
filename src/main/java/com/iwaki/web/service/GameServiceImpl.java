package com.iwaki.web.service;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.cache.RedisManager;
import com.iwaki.web.model.Award;
import com.iwaki.web.model.AwardPrice;
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
				rank.setScore(jedis.zscore(key, pKey).longValue());
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
	
	@Override
	public boolean helpAward(String ip, String openid) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = helperKey(openid);
			jedis.sadd(key, ip);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return true;
	}

	@Override
	public Award getGuestAward(String ip) {
		Award a = new Award();
		Random r = new Random();
		a.setLevel(5 + "");
		a.setDesc("游客您好！恭喜您获得5等奖哦！");
		a.setCode(1000000 + r.nextInt(9000000) + "");
		return a;
	}

	@Override
	public Award getFansAward(String openid) {
		Award a = new Award();
		Random r = new Random();
		int level = r.nextInt(5) + 1;
		a.setLevel(level + "");
		a.setDesc("亲爱的粉丝您好！恭喜您获得" + a.getLevel() + "等奖(" + "价值" + AwardPrice.values()[level - 1].getPrice() + "元)");
		a.setCode(1000000 + r.nextInt(9000000) + "");
		return a;
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
	
	private String helperKey(String openid) {
		return "award_help:" + openid + ":";
	}

	

}
