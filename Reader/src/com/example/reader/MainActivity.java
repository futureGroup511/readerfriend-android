package com.example.reader;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.Main.Config;
import com.example.Main.service.HandlerMessage;
import com.example.Main.service.HttpsService;
import com.karics.library.zxing.android.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final int REQUEST_CODE_SCAN = 0x0000;
	private static final String DECODED_CONTENT_KEY = "codedContent";
	private String[] data = {"借书","还书"};
	
	private Handler handler = new Handler();    
    
    private Runnable runable =new Runnable() {    
       public void run() {    
           // TODOAuto-generated method stub  
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
             handler.postDelayed(this,60*1000);//设置延迟时间，此处是5秒  
              //需要执行的代码  
       }     
    };  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1,data);
		ListView listView = (ListView)findViewById(R.id.list_View);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		handler.post(runable);//每两秒执行一次runnable.
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			Intent intent = new Intent(MainActivity.this,
					CaptureActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SCAN);
			
			break;
		case 1:
			Intent intent1 = new Intent(MainActivity.this,
					CaptureActivity.class);
			startActivityForResult(intent1, REQUEST_CODE_SCAN);
			break;

		default:
			break;
		}
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//写下你希望按下返回键达到的效果代码，不写则不会有反应
			moveTaskToBack(true);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
