package com.iwaki.app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {

	public static void main(String args[]) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long c = 1415409076968L - System.currentTimeMillis();
		
		Date date = new Date(1415385192818L - 28382132L);
		System.out.println(sdf.format(date));
	}
	
	
}
