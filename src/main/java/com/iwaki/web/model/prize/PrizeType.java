package com.iwaki.web.model.prize;

public enum PrizeType {

	LEVEL_1(1188, "iwaki国王系列福袋7件套", 1, 1),
	LEVEL_2(268, "iwaki梦幻耐热冷水壶", 2, 1),
	LEVEL_3(18, "2015年国王台历", 3, 1),
	LEVEL_4(50, "50元iwaki怡万家天猫旗舰店优惠券", 4, 0),
	LEVEL_5(20, "20元iwaki怡万家天猫旗舰店优惠券", 5, 0),
	LEVEL_6(10, "10元iwaki怡万家天猫旗舰店优惠券", 6, 0);
	
	private int price;
	private int level;
	private String prizeName;
	private int type;// 0 优惠券 1 实物
	
	PrizeType(int price,String prizeName, int level, int type) {
		this.price = price;
		this.prizeName = prizeName;
		this.level = level;
		this.type = type;
	}


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}


	public String getPrizeName() {
		return prizeName;
	}


	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}
}
