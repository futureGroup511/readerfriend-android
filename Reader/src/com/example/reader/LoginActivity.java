package com.example.reader;



import java.security.MessageDigest;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import com.example.Main.Config;
import com.example.Main.service.HttpsService;
import com.example.Main.service.MyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	
	TextView forget;//�޸�����
	Button login;//��¼��ť
	EditText passWord;//����
	EditText name;//�˺�
	static String str;
	private Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
        };
    };
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		passWord = (EditText) findViewById(R.id.inputPassWords);
		name = (EditText) findViewById(R.id.inputNames);
		forget = (TextView) findViewById(R.id.fogivePassWord);
		login = (Button) findViewById(R.id.loginSucsuss);
		forget.setOnClickListener(this);
		login.setOnClickListener(this);
	}
	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fogivePassWord:
			//����޸������TextView
//			Intent itentc = new Intent(LoginActivity.this,
//					CheckerActivity.class);
//			startActivity(itentc);
			Toast.makeText(LoginActivity.this, "�˹�����δ���ţ�����ϰ��̨��Ա", 1000).show();
			break;
		case R.id.loginSucsuss:
			//�����½��ť����Ҫд������
			String names = name.getText().toString();
			String pass = passWord.getText().toString();
			
			
			if (names.equals("") || names == null) {
				Toast.makeText(LoginActivity.this, "�������˺�", Toast.LENGTH_SHORT)
						.show();
				//bar.setVisibility(View.GONE);
				return;
			}

			if (pass.equals("") || pass == null) {
				Toast.makeText(LoginActivity.this, "����������", Toast.LENGTH_SHORT)
						.show();
				//bar.setVisibility(View.GONE);
				return;
			}
			try {
				HttpsService httpsService = HttpsService.getInstance();
		
				try {
					new Thread(new Runnable(){

						@Override
						public void run() {
						RequestBody requestBody=RequestBody.create(MediaType.parse("text/html"),sendJSON().toString());  
							Request request = new Request.Builder().url(Config.SERVER_IP+Config.PREFIX+Config.METHOD_LOGIN)
									.post(requestBody)
									.build();
							try{
								
								 OkHttpClient client = new OkHttpClient();
								//Response response = client.newCall(request).execute();
								Response response = HttpsService.getInstance().getResponse(request);
								str = response.body().string();
								System.out.println("temp34:"+str);
									JSONObject js = new JSONObject(str);
										if(js!=null) {
											
										String result = js.getString("result");
										System.out.println(result);
										System.out.println(result.equals("0"));
										 HttpsService.getInstance().setToken(js.getString("token"));
										switch (result) {
											case "0":	
												 
												MyUtil.showToast(LoginActivity.this, "��¼�ɹ�");
											
												Intent intent = new Intent(LoginActivity.this,
														MainActivity.class);
												startActivity(intent);
												finish();
												
												break;
											case "10001":
												Intent intent2 = new Intent(LoginActivity.this,CheckerActivity.class);											
												startActivity(intent2);
												finish();
												Looper.loop();
												break;
											case "10003":
												Message msg3 = new Message();
								                msg3.obj = "�������";
								                // ����Ϣ���͵����̣߳������߳�����ʵToast
								                handler.sendMessage(msg3);
												
												
											case "10004":
												Message msg4 = new Message();
								                msg4.obj = "�˺Ų�����";
								                // ����Ϣ���͵����̣߳������߳�����ʵToast
								                handler.sendMessage(msg4);
												break;
											default:
												break;
									}
								}else{
									Message msg = new Message();
					                msg.obj = "û�����ӵ�������";
					                // ����Ϣ���͵����̣߳������߳�����ʵToast
					                handler.sendMessage(msg);
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
				catch(Exception e){
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}


	public JSONObject sendJSON() {
		String pass = passWord.getText().toString();
		String names = name.getText().toString();
		int rand = (int)(Math.random()*9000+1000);
		String passes = names+pass+rand;
		try {
			JSONObject person = new JSONObject();
			person.put("phone", names);
			person.put("password", getSha1(passes));
			person.put("rand",""+rand);
			return person;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	 public static String getSha1(String str){//sha1�����㷨
	        if(str==null||str.length()==0){
	            return null;
	        }
	        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
	                'a','b','c','d','e','f'};
	        try {
	            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
	            mdTemp.update(str.getBytes("UTF-8"));

	            byte[] md = mdTemp.digest();
	            int j = md.length;
	            char buf[] = new char[j*2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                buf[k++] = hexDigits[byte0 & 0xf];      
	            }
	            return new String(buf);
	        } catch (Exception e) {
	            // TODO: handle exception
	            return null;
	        }
	    }
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				//д����ϣ�����·��ؼ��ﵽ��Ч�����룬��д�򲻻��з�Ӧ
				moveTaskToBack(true);
				return false;
			}
			return super.onKeyDown(keyCode, event);
		}
}
