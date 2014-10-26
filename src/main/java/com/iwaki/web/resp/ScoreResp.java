package com.iwaki.web.resp;

import java.util.ArrayList;

import com.iwaki.web.model.ScoreRank;

public class ScoreResp extends Resp {

	private long myRank;
	
	private ArrayList<ScoreRank> ranks;

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
}
