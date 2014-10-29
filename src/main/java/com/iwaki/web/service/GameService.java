package com.iwaki.web.service;

import java.util.ArrayList;

import com.iwaki.web.model.ScoreRank;

public interface GameService {

	public void incrSharedCount(String openid, String gamerIp);
	
	public long incrRank(ScoreRank scoreRank);
	
	public ArrayList<ScoreRank> topRanks();
	
	public void acceptArticle(String openid);
	
	public boolean hasAcceptArticle(String openid);
}
