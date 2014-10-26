package com.iwaki.web.model;

import java.util.ArrayList;

/**
 * 分享者
 * @author Administrator
 *
 */
public class Sharer {

	/**
	 * 分享者openid
	 */
	private String openid;
	
	/**
	 * 从分享链接进入的玩家ip
	 */
	private ArrayList<String> gamerIps;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public ArrayList<String> getGamerIps() {
		return gamerIps;
	}

	public void setGamerIps(ArrayList<String> gamerIps) {
		this.gamerIps = gamerIps;
	}
}
