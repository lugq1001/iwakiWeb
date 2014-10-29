package com.iwaki.web.model;

public enum AwardPrice {

	LEVEL_1(1000),
	LEVEL_2(300),
	LEVEL_3(100),
	LEVEL_4(50),
	LEVEL_5(20);
	
	private int price;

	
	AwardPrice(int price) {
		this.price = price;

	}


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}
}
