package com.iwaki.web.resp;

public class GetAwardResp extends Resp {

	// 0 优惠券 1实物
	private String type = "0";
	
	// 价值
	private String price = "";
	
	// 兑换码
	private String code = "";
	
	// 实物名称
	private String award_name = "";
	
	// 实物名称
	private String level = "";
	
	// 一等奖附加提示
	private String tips = "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAward_name() {
		return award_name;
	}

	public void setAward_name(String award_name) {
		this.award_name = award_name;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
}
