package com.iwaki.web.resp;

import java.util.ArrayList;

import com.iwaki.web.model.Award;
import com.iwaki.web.model.ScoreRank;

public class ScoreResp extends Resp {

	private long myRank;
	
	private boolean atl = false;
	
	private ArrayList<ScoreRank> ranks;
	
	private Award award;
	
	private boolean awardResult;
	
	private String awardDesc;

	public long getMyRank() {
		return myRank;
	}

	public void setMyRank(long myRank) {
		this.myRank = myRank;
	}

	public ArrayList<ScoreRank> getRanks() {
		return ranks;
	}

	public void setRanks(ArrayList<ScoreRank> ranks) {
		this.ranks = ranks;
	}

	public boolean isAtl() {
		return atl;
	}

	public void setAtl(boolean atl) {
		this.atl = atl;
	}

	public Award getAward() {
		return award;
	}

	public void setAward(Award award) {
		this.award = award;
	}

	public boolean isAwardResult() {
		return awardResult;
	}

	public void setAwardResult(boolean awardResult) {
		this.awardResult = awardResult;
	}

	public String getAwardDesc() {
		return awardDesc;
	}

	public void setAwardDesc(String awardDesc) {
		this.awardDesc = awardDesc;
	}

	
}
