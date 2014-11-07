package com.iwaki.web.model;

public class ScoreRank {

	private String openid;
	
	private String nickname;
	
	private String avatar;
	
	private long score;
	
	public static ScoreRank topRankUser(String openid, String nickname, long score) {
		ScoreRank r = new ScoreRank();
		r.setOpenid(openid);
		r.setScore(score);
		r.setNickname(nickname);
		r.setAvatar("");
		return r;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}
}
