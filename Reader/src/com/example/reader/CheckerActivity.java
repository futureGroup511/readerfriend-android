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
					System.out.println("���뵽���߳���");
					RequestBody requestBody=RequestBody.create(MediaType.parse("text/html"),sendJSON().toString());
					Request request = HttpsService.getInstance().getMyRequest(requestBody, Config.getAllUrl(Config.Check_Number));
					try{
						OkHttpClient client = new OkHttpClient();
						//Response response = client.newCall(request).execute();
						Response response = HttpsService.getInstance().getResponse(request);
						str = response.body().string();
						System.out.println("���ص��ַ�����"+str);
						JSONObject js = new JSONObject(str);
						
						
						String result = js.getString("result");
						System.out.println(result);
						switch (result) {
							case "0":
								MyUtil.showToast(CheckerActivity.this, "��¼�ɹ�");
								Intent intent = new Intent(CheckerActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
								break;
							case "10002":
								MyUtil.showToast(CheckerActivity.this, "�˺ű�����,����ϵ����Ա");
								
								break;
							case "10005":
								
								MyUtil.showToast(CheckerActivity.this, "��¼��֤�����");
								break;
							case "-1":
								MyUtil.showToast(CheckerActivity.this, "�����ʽ�д���");
								break;
							case "-2":
								MyUtil.showToast(CheckerActivity.this, "���Ѿ���ʱ�������µ�¼");
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
			//д����ϣ�����·��ؼ��ﵽ��Ч�����룬��д�򲻻��з�Ӧ
			Intent intent = new Intent(CheckerActivity.this,LoginActivity.class);
			startActivity(intent);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
