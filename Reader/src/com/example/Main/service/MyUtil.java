package com.example.Main.service;

import android.app.Activity;
import android.widget.Toast;

public class MyUtil {


	    /**
	     * ��ʾtoast
	     * @param ctx
	     * @param msg
	     */
	    public static void showToast(final Activity ctx,final String msg){
	        // �ж��������̣߳��������߳�
	        if("main".equals(Thread.currentThread().getName())){
	            Toast.makeText(ctx, msg, 0).show();
	        }else{
	            // ���߳�
	            ctx.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                    Toast.makeText(ctx, msg, 0).show();
	                }
	            });
	        }
	    }

}
