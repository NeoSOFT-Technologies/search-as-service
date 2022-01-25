package com.searchservice.app.domain.utils;

public class ThrottlerUtils {
	public static double formatRequestSizeStringToDouble(String size) {
		String numericValue = size.substring(0, size.length()-2);
		return Double.parseDouble(numericValue);
	}
	
	public static double getSizeInkBs(String data) {
		int numberOfChars = data.length();
		return (double) (8 * (int)((double)((numberOfChars * 2) + 45) / 8))/1000;
	}
	
	private ThrottlerUtils() {
	}
}
