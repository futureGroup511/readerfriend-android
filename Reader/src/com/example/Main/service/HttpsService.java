package com.example.Main.service;

import java.io.IOException;
import java.util.Collections;

import com.example.Main.Config;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/*
@user song
@date 2017骞�5鏈�13鏃�
@todo TODO
*/
public class HttpsService {
	private OkHttpClient client = null;
	private String token = "";
	private static HttpsService instance = null;
	private HttpsService(){
		ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)  
			    .tlsVersions(TlsVersion.TLS_1_2)
			    .build();
		client = new OkHttpClient.Builder() 
			    .connectionSpecs(Collections.singletonList(spec))
			    .build();
	}
	
	public Response getResponse(Request request) throws IOException{
		System.err.println("请求网址："+request.url());
		String str = client.newCall(request).execute().networkResponse().message();
		System.out.println("testfdsfasf...........");
		System.err.println(str);
		return client.newCall(request).execute();
	}
	
	public Request getMyRequest(RequestBody requestBody,String url){
		Request request = new Request.Builder().url(url)
				.post(requestBody)
				.addHeader("token", token)
				.build();
		return request;
	}
	
	public static HttpsService getInstance(){
		if(instance == null){
			synchronized (HttpsService.class) {
				if(instance == null){
					instance =  new HttpsService();
				}
			}
		}
		return instance;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
