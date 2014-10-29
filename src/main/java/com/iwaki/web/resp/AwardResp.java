package com.iwaki.web.resp;

import com.iwaki.web.model.Award;

public class AwardResp extends Resp {

	private Award award;
	
	private String helpUrl;

	public Award getAward() {
		return award;
	}

	public void setAward(Award award) {
		this.award = award;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}
	
}
