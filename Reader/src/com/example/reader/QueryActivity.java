package com.example.reader;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;

import com.karics.library.zxing.android.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QueryActivity extends Activity{
	private TextView tv_dialog;
	private String[] Names;
	private String[] Images;
	private String[] Isbn;
	private String[] book = {"书名:","序列:"};
	private static StringBuffer buffer =null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);
		initView();
		Intent intent = getIntent() ;
		if(intent.getStringArrayExtra("name")!=null){
		   Names = intent.getStringArrayExtra("name");
		   Images = intent.getStringArrayExtra("image");
		   Isbn = intent.getStringArrayExtra("isbn");
		   show(Names,Images,Isbn);
		}
	}
	private void show(String[] names2, String[] images2, String[] isbn2) {
		// TODO Auto-generated method stub
		buffer = new StringBuffer("\n");
		for(int i=0; i<names2.length; i++){
			buffer.append(book[0]).append(names2[i]+"\n");
			buffer.append(book[1]).append(isbn2[i]+"\n");
			buffer.append("\n");
		}
		tv_dialog.setText(buffer.toString());
	}
	private void initView() {
		// TODO Auto-generated method stub
		tv_dialog =(TextView)findViewById(R.id.tv_dialog);
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//写下你希望按下返回键达到的效果代码，不写则不会有反应
			Intent intent = new Intent(QueryActivity.this,MainActivity.class);
			startActivity(intent);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	

}
