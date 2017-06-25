package com.karics.library.zxing.android;

import com.example.Main.Config;
import com.example.Main.service.HttpsService;
import com.example.Main.service.MyUtil;
import com.example.reader.CheckerActivity;
import com.example.reader.LoginActivity;
import com.example.reader.QueryActivity;
import com.example.reader.R;
import com.example.reader.ResultActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.karics.library.zxing.android.BeepManager;
import com.karics.library.zxing.android.CaptureActivityHandler;
import com.karics.library.zxing.android.FinishListener;
import com.karics.library.zxing.android.InactivityTimer;
import com.karics.library.zxing.android.IntentSource;
import com.karics.library.zxing.camera.CameraManager;
import com.karics.library.zxing.view.ViewfinderView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 杩欎釜activity鎵撳紑鐩告満锛屽湪鍚庡彴绾跨▼鍋氬父瑙勭殑鎵弿锛涘畠缁樺埗浜嗕竴涓粨鏋渧iew鏉ュ府鍔╂纭湴鏄剧ず鏉″舰鐮侊紝鍦ㄦ壂鎻忕殑鏃跺�欐樉绀哄弽棣堜俊鎭紝
 * 鐒跺悗鍦ㄦ壂鎻忔垚鍔熺殑鏃跺�欒鐩栨壂鎻忕粨鏋�
 * 
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();
	static String str;
	private JSONObject js = null;
	private String flag;
	// 鐩告満鎺у埗
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	// 鐢甸噺鎺у埗
	private InactivityTimer inactivityTimer;
	// 澹伴煶銆侀渿鍔ㄦ帶鍒�
	private BeepManager beepManager;

	private ImageButton imageButton_back;

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * OnCreate涓垵濮嬪寲涓�浜涜緟鍔╃被锛屽InactivityTimer锛堜紤鐪狅級銆丅eep锛堝０闊筹級浠ュ強AmbientLight锛堥棯鍏夌伅锛�
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// 淇濇寔Activity澶勪簬鍞ら啋鐘舵��
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);

		hasSurface = false;

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		imageButton_back = (ImageButton) findViewById(R.id.capture_imageview_back);
		imageButton_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager蹇呴』鍦ㄨ繖閲屽垵濮嬪寲锛岃�屼笉鏄湪onCreate()涓��
		// 杩欐槸蹇呴』鐨勶紝鍥犱负褰撴垜浠涓�娆¤繘鍏ユ椂闇�瑕佹樉绀哄府鍔╅〉锛屾垜浠苟涓嶆兂鎵撳紑Camera,娴嬮噺灞忓箷澶у皬
		// 褰撴壂鎻忔鐨勫昂瀵镐笉姝ｇ‘鏃朵細鍑虹幇bug
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// activity鍦╬aused鏃朵絾涓嶄細stopped,鍥犳surface浠嶆棫瀛樺湪锛�
			// surfaceCreated()涓嶄細璋冪敤锛屽洜姝ゅ湪杩欓噷鍒濆鍖朿amera
			initCamera(surfaceHolder);
		} else {
			// 閲嶇疆callback锛岀瓑寰卻urfaceCreated()鏉ュ垵濮嬪寲camera
			surfaceHolder.addCallback(this);
		}

		beepManager.updatePrefs();
		inactivityTimer.onResume();

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 鎵弿鎴愬姛锛屽鐞嗗弽棣堜俊鎭�
	 * 
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 *
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor){
		inactivityTimer.onActivity();
		String resultString = rawResult.getText();
		try {
			js = new JSONObject(resultString);
			flag = js.getString("id");
			System.out.println("flag的结果是："+flag);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			if(flag==null){
			beepManager.playBeepSoundAndVibrate();
			 HttpsService httpsService = HttpsService.getInstance();
						try{
							new Thread(new Runnable() {
								
								@Override
								public void run(){
									RequestBody requestBody=RequestBody.create(MediaType.parse("text/html"),js.toString());
									
									Request request = HttpsService.getInstance().getMyRequest(requestBody, Config.getAllUrl(Config.BORROWBOOK));
									try{
									
										OkHttpClient client = new OkHttpClient();
										//Response response = client.newCall(request).execute();
										
										Response response = HttpsService.getInstance().getResponse(request);
										str = response.body().string();
										System.out.println("反悔的数据是"+str);
										JSONObject Js2 = new JSONObject(str);
										String result = Js2.getString("result");
										if(result.equals("0")){
											JSONArray books = Js2.getJSONArray("books");
											String[] Names = new String[books.length()];
											String[] Images = new String[books.length()];
											String[] Isbn = new String[books.length()];
											Intent intent = new Intent(CaptureActivity.this, QueryActivity.class);
											Bundle bundle = new Bundle();
											for(int i=0; i<books.length(); i++){
												Names[i] = books.getJSONObject(i).getString("name");
												Images[i] = books.getJSONObject(i).getString("image");
												Isbn[i] = books.getJSONObject(i).getString("isbn");
											}
											 bundle.putSerializable("name",Names);
											 bundle.putSerializable("image",Images);
											 bundle.putSerializable("isbn",Isbn);
											 intent.putExtras(bundle);
											startActivity(intent);
											finish();
										}else{
											System.err.println("进入了111111111111");
											Intent intent = new Intent(CaptureActivity.this, QueryActivity.class);
											startActivity(intent);
											finish();
										}
									}catch(Exception e){
										
									}				
									
								}
							}).start();
						}catch(Exception e){
							
						} 
			}else{
				beepManager.playBeepSoundAndVibrate();
				 HttpsService httpsService = HttpsService.getInstance();
							try{
								new Thread(new Runnable() {
									
									@Override
									public void run(){
										RequestBody requestBody=RequestBody.create(MediaType.parse("text/html"),js.toString());
										
										Request request = HttpsService.getInstance().getMyRequest(requestBody, Config.getAllUrl(Config.RETURNBOOK));
										try{
										
											OkHttpClient client = new OkHttpClient();
											//Response response = client.newCall(request).execute();
											
											Response response = HttpsService.getInstance().getResponse(request);
											str = response.body().string();
											System.out.println("反悔的数据是"+str);
											JSONObject Js2 = new JSONObject(str);
											String result = Js2.getString("result");
											if(result.equals("0")){
												Intent intent = new Intent(CaptureActivity.this, ResultActivity.class);
												startActivity(intent);
												finish();
											}else{
												Intent intent = new Intent(CaptureActivity.this, ResultActivity.class);
												intent.putExtra("result", "10021");
												startActivity(intent);
												finish();
											}
										}catch(Exception e){
											
										}				
										
									}
								}).start();
							}catch(Exception e){
								
							} 
			}
//			Intent intent = new Intent(CaptureActivity.this, QueryActivity.class);
//			intent.putExtra("codedContent", rawResult.getText());
//			intent.putExtra("codedBitmap", barcode);
//			setResult(RESULT_OK, intent);
//			startActivity(intent);
//			finish();
		}
			
		

	}

	/**
	 * 鍒濆鍖朇amera
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			// 鎵撳紑Camera纭欢璁惧
			cameraManager.openDriver(surfaceHolder);
			// 鍒涘缓涓�涓猦andler鏉ユ墦寮�棰勮锛屽苟鎶涘嚭涓�涓繍琛屾椂寮傚父
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 鏄剧ず搴曞眰閿欒淇℃伅骞堕��鍑哄簲鐢�
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}
	/*跳转*/
	

}
