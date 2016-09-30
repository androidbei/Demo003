package com.example.demo003.util;

public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}
