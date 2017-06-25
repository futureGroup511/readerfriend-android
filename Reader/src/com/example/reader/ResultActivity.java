package com.example.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;


public class ResultActivity extends Activity {

	private TextView tv_result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		initView();
		Intent intent = getIntent();
		if(intent.getStringExtra("result").equals("10021")){
			tv_result.setText("����ʧ�ܣ�����ϵ������Ա");
		}
	}
	private void initView() {
		// TODO Auto-generated method stub
		tv_result = (TextView) findViewById(R.id.tv_result);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//д����ϣ�����·��ؼ��ﵽ��Ч�����룬��д�򲻻��з�Ӧ
			Intent intent = new Intent(ResultActivity.this,MainActivity.class);
			startActivity(intent);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
