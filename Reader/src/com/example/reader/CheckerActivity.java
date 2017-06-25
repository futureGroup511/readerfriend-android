package com.example.reader;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.Main.Config;
import com.example.Main.service.HttpsService;
import com.example.Main.service.MyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CheckerActivity extends Activity implements OnClickListener {
	
	private EditText edit;
	private Button btn;
	static String str;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checker);
		initView();
	}
	private void initView() {
		edit = (EditText) findViewById(R.id.et);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		HttpsService httpsService = HttpsService.getInstance();
		try{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("进入到多线程中");
					RequestBody requestBody=RequestBody.create(MediaType.parse("text/html"),sendJSON().toString());
					Request request = HttpsService.getInstance().getMyRequest(requestBody, Config.getAllUrl(Config.Check_Number));
					try{
						OkHttpClient client = new OkHttpClient();
						//Response response = client.newCall(request).execute();
						Response response = HttpsService.getInstance().getResponse(request);
						str = response.body().string();
						System.out.println("返回的字符串是"+str);
						JSONObject js = new JSONObject(str);
						
						
						String result = js.getString("result");
						System.out.println(result);
						switch (result) {
							case "0":
								MyUtil.showToast(CheckerActivity.this, "登录成功");
								Intent intent = new Intent(CheckerActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
								break;
							case "10002":
								MyUtil.showToast(CheckerActivity.this, "账号被锁定,请联系管理员");
								
								break;
							case "10005":
								
								MyUtil.showToast(CheckerActivity.this, "登录验证码错误");
								break;
							case "-1":
								MyUtil.showToast(CheckerActivity.this, "请求格式有错误");
								break;
							case "-2":
								MyUtil.showToast(CheckerActivity.this, "您已经超时，请重新登录");
								finish();
								break;
							default:
								break;
						}
				}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public JSONObject sendJSON() {
		String checker = edit.getText().toString();
		try {
			JSONObject person = new JSONObject();
			person.put("vCode", checker);
			return person;

		} catch (Exception e) {
			
		}
		return null;

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//写下你希望按下返回键达到的效果代码，不写则不会有反应
			Intent intent = new Intent(CheckerActivity.this,LoginActivity.class);
			startActivity(intent);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
