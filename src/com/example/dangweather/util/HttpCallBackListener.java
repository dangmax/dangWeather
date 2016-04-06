package com.example.dangweather.util;

public interface HttpCallBackListener {
	void onFinish(String content);
	
	void onError(Exception e);
}
