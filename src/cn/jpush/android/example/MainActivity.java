package cn.jpush.android.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

import com.weibo.sdk.android.api.*;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface; import  com.ycao.message.R;

public class MainActivity extends InstrumentedActivity implements OnClickListener{
	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "929887641";
	private static final String REDIRECT_URL = "http://citsm.sinaapp.com/mypushadd.php";
	public static Oauth2AccessToken accessToken;
	public static final String TAG = "mypush";
	private Button mInit;
	private Button mSetting;
	private Button mStopPush;
	private Button mResumePush;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView(); 
		Button btnWeibo = (Button) findViewById(R.id.initWeibo);;
		btnWeibo.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
			
				initSinaWeibo();
				
			}
		});
		
	}
	private void initSinaWeibo(){
			Log.i(TAG,"initWeibo");
		  mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		  mWeibo.authorize(MainActivity.this, new AuthDialogListener());
		
	}
	 class AuthDialogListener implements WeiboAuthListener {
	        public void onComplete(Bundle values) {
	            String token = values.getString("access_token");
	            String expires_in = values.getString("expires_in");
	            MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
	            if (MainActivity.accessToken.isSessionValid()) {
	                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(MainActivity.accessToken.getExpiresTime()));
	                AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);

	        		TextView mweibo = (TextView) findViewById(R.id.weibo);
	        		mweibo.setText("token: " + token);
	        		AccountAPI aca = new AccountAPI(MainActivity.accessToken);
	        		aca.getUid(new RequestListener(){
						public void onComplete(String arg0) {
							//Log.i(TAG,arg0);
							// Toast.makeText(getApplicationContext(), "getUid : " + arg0, Toast.LENGTH_LONG).show();
							 try {
								 JSONTokener jsonParser = new JSONTokener(arg0); 
								 JSONObject person = (JSONObject) jsonParser.nextValue();  
								 String uid  =  person.getString("uid");
								
								UsersAPI ua = new UsersAPI(MainActivity.accessToken);
							
								  StatusesAPI api =  new StatusesAPI(MainActivity.accessToken);
								  api.update("你好世界222", "", "",new RequestListener(){

									public void onComplete(String arg0) {
										 Log.i(TAG,arg0);
										
									}

									public void onError(WeiboException arg0) {
										// TODO Auto-generated method stub
										
									}

									public void onIOException(IOException arg0) {
										// TODO Auto-generated method stub
										
									}
									  
								
								  });
								  
								ua.show(Long.parseLong(uid), new RequestListener(){
									public void onComplete(String arg0) {
										 Log.i(TAG,arg0);
										try {
											JSONTokener jsonParser = new JSONTokener(arg0); 
										 
											JSONObject person = (JSONObject) jsonParser.nextValue();
											   String strname  =  person.getString("name");
											 	Log.i(TAG,strname);
											 	String[] sArray = ("android,all,"+strname).split(",");
											 	
												Set<String> tagSet = new HashSet<String>();
												
												for (String sTagItme : sArray) {
													if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
														
														return;
													}
													tagSet.add(sTagItme);
												}
											 	JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet);
												SharedPreferences preferences = getSharedPreferences("mypush", MODE_PRIVATE);
										        SharedPreferences.Editor editor = preferences.edit();
										        editor.putString("tagName", ("android,all,"+strname));
										        editor.commit();
										        
										   
										     
										        
											
										} catch (JSONException e) {
											
										}  
										
									}

									public void onError(WeiboException arg0) {
										
										
									}

									public void onIOException(IOException arg0) {
									
										
									}
									
									
								});
								
							} catch (JSONException e) {
								
								
							}
							  
						}

						public void onError(WeiboException arg0) {
						
							//Toast.makeText(MainActivity.this, "onError"+arg0, Toast.LENGTH_SHORT).show();
						}
						public void onIOException(IOException arg0) {
							//Toast.makeText(MainActivity.this, "onIOException"+arg0, Toast.LENGTH_SHORT).show();
							
						}
	        		});
	        		//Toast.makeText(MainActivity.this, "授权成功"+token+expires_in, Toast.LENGTH_SHORT).show();
	            }
	        }

	        public void onError(WeiboDialogError e) {
	            Toast.makeText(getApplicationContext(),
	                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
	        }

	        public void onCancel() {
	            Toast.makeText(getApplicationContext(), "Auth cancel",
	                    Toast.LENGTH_LONG).show();
	        }

	        public void onWeiboException(WeiboException e) {
	            Toast.makeText(getApplicationContext(),
	                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
	                    .show();
	        }

	    }

	private void initView(){
		TextView mImei = (TextView) findViewById(R.id.tv_imei);
		String udid =  JPushInterface.getUdid(getApplicationContext());
        if (null != udid) mImei.setText("IMEI: " + udid);
        
		TextView mAppKey = (TextView) findViewById(R.id.tv_appkey);
		String appKey = ExampleUtil.getAppKey(getApplicationContext());
		if (null == appKey) appKey = "AppKey异常";
		mAppKey.setText("AppKey: " + appKey);

		String packageName =  getPackageName();
		TextView mPackage = (TextView) findViewById(R.id.tv_package);
		mPackage.setText("PackageName: " + packageName);

		String versionName =  ExampleUtil.GetVersion(getApplicationContext());
		TextView mVersion = (TextView) findViewById(R.id.tv_version);
		mVersion.setText("Version: " + versionName);

		
		
		SharedPreferences preferences = getSharedPreferences("mypush", MODE_PRIVATE);
      
		String tagName = preferences.getString("tagName","");
		TextView mTagNmae = (TextView) findViewById(R.id.tag_name);
		mTagNmae.setText("tagName: " + tagName);
		
		

		
	    mInit = (Button)findViewById(R.id.init);
		mInit.setOnClickListener(this);

//		mStopPush = (Button)findViewById(R.id.stopPush);
//		mStopPush.setOnClickListener(this);

		mResumePush = (Button)findViewById(R.id.resumePush);
		mResumePush.setOnClickListener(this);


		

	}
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.init:
			init();
			break;
//		case R.id.setting:
//			Intent intent = new Intent(MainActivity.this, PushSetActivity.class);
//			startActivity(intent);
//			break;
//		
		case R.id.resumePush:
			JPushInterface.resumePush(getApplicationContext());
			break;
		}
	}

	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	private void init(){
		JPushInterface.init(getApplicationContext());
	}
}