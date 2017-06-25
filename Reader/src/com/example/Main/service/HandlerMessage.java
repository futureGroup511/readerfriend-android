package com.example.Main.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.Main.Config;
import com.example.reader.QueryActivity;
import com.example.reader.ResultActivity;
import com.karics.library.zxing.android.CaptureActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerMessage {
	Handler handler = new Handler();
	Runnable runnable=new Runnable() {  
	    @Override  
	    public void run() {  
	        // TODO Auto-generated method stub  
	        //要做的事情  
	    	HttpsService httpsService = HttpsService.getInstance();
			try{
				new Thread(new Runnable() {
					
					@Override
					public void run(){
						RequestBody requestBody=RequestBody.create(MediaType.parse("text/html"),"");
						
						Request request = HttpsService.getInstance().getMyRequest(requestBody, Config.SERVER_IP+Config.PINGTOKEN);
						try{
						
							OkHttpClient client = new OkHttpClient();
							//Response response = client.newCall(request).execute();
							
							Response response = HttpsService.getInstance().getResponse(request);
					
					
						}catch(Exception e){
							
						}				
						
					}
				}).start();
			}catch(Exception e){
				
			} 
	        handler.postDelayed(this, 2000);  
	    }  
	};  
}
