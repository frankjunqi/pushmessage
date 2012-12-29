package cn.jpush.android.example;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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


import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import com.ycao.message.R;

public class MainActivity extends InstrumentedActivity implements OnClickListener {
	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "929887641";
	private static final String REDIRECT_URL = "http://citsm.sinaapp.com/mypushadd.php";
	public static Oauth2AccessToken accessToken;
	public static final String TAG = "mypush";
	private Button mInit;
	private Button mResumePush;
	private ViewPager mViewPager;
	public View view1 = null;
	public View view2 = null;
	public View view3 = null;
	private ImageView cursor;// 动画图片
	private TextView t1, t2, t3;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	public String id = "";
	public ArrayList<View> views = null;
	public LocationManager manager=null;
	public SharedPreferences sp = null;
	public MyLocationListener myLocationListener=null;
	SimpleAdapter adapter = null;
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	ArrayList<String> list1 = new ArrayList<String>();
	ImageGetter imgGetter = new Html.ImageGetter() {
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			drawable = Drawable.createFromPath(source);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			return drawable;
		}
	};

	protected void onResume() {
		
		
		Log.i(TAG, "Activity1 onResume called!");
		super.onResume();

		SharedPreferences preferences = getSharedPreferences("mypush", MODE_PRIVATE);
		String lastId = preferences.getString("lastId", "");

		if (!lastId.equals("")) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("lastId", "");
			editor.commit();
			String id = lastId;

			mViewPager.setCurrentItem(2);
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", id));
			String param = URLEncodedUtils.format(params, "UTF-8");
			String baseUrl = "http://citsm.sinaapp.com/getpush.php";
			HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpResponse response = httpClient.execute(getMethod); // 发起GET请求
				TextView tv = (TextView) view3.findViewById(R.id.txtView);
				tv.setText(Html.fromHtml(EntityUtils.toString(response.getEntity(), "utf-8"), imgGetter, null));
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		sp = getSharedPreferences("mypush",MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("lastId", "");
		editor.commit();
		
		
		 
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.lay1, null);
		view2 = mLi.inflate(R.layout.lay2, null);
		view3 = mLi.inflate(R.layout.lay3, null);

		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			public int getCount() {
				return views.size();
			}

			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		mViewPager.setAdapter(mPagerAdapter);

		InitImageView();
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		initView();
		
		Button btnWeibo = (Button) view1.findViewById(R.id.initWeibo);
		btnWeibo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initSinaWeibo();
			}
		});
		
		Button button1 =  (Button) view3.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendWeibo();
			}
		});
		Button gps =  (Button) view1.findViewById(R.id.gps);
		gps.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Button gps =  (Button)v.findViewById(R.id.gps);
				
				String lastgps = sp.getString("lastgps", "");
				SharedPreferences.Editor editor = sp.edit();
				
				if(lastgps.equals("开启定位")){
					gps.setText("关闭定位");
				
					editor.putString("lastgps", "关闭定位");
					editor.commit();
					initGPRS();
				}else{
					gps.setText("开启定位");
					editor.putString("lastgps", "开启定位");
					editor.commit();
					manager.removeUpdates(myLocationListener);  
				}
				
			}
		});
		
		
		Button button3 =  (Button) view1.findViewById(R.id.pushmsg);
		button3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
				SharedPreferences preferences = getSharedPreferences("mypush", MODE_PRIVATE);
				String tagName = preferences.getString("tagName", "");

				String[] sArray = tagName.split(",");
				Log.i(TAG,sArray[sArray.length-1]);
				params.add(new BasicNameValuePair("push", sArray[sArray.length-1]));
				
			
				 
				params.add(new BasicNameValuePair("msg", "自推:"+ sp.getString("lastloaction", "")));
				String param = URLEncodedUtils.format(params, "UTF-8");
				String baseUrl = "http://citsm.sinaapp.com/sg.php";
				HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

				HttpClient httpClient = new DefaultHttpClient();

				try {
					HttpResponse response = httpClient.execute(getMethod); 
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Bundle extra = new Bundle();
		extra = getIntent().getExtras();
		
		final ListView listview = (ListView)view2.findViewById(R.id.listview);
		
		

		adapter = new SimpleAdapter(this,list,R.layout.lv,new String[]{"img","title","info"},new int[]{R.id.img,R.id.title,R.id.info});
		getList();
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				 HashMap<String,String> map=(HashMap<String,String>)listview.getItemAtPosition(arg2); 
				
				
              

              
                Handler handler = new Handler();
                
//                Runnable runnable=new Runnable() {
//
//                    public void run() {
//                    	 EditText textwibo = (EditText)view2.findViewById(R.id.editText1);
//                    	 textwibo.setText("abcd");
//                    }
//                };
//                handler.postDelayed(runnable, 1000);
				mViewPager.setCurrentItem(2);
				 
			}
             
        });
		//setListAdapter(adapter);
		//ListView listView = (ListView)view2.findViewById(R.id.ListView01);
       // listView.setAdapter(new ArrayAdapter<String>(this, R.layout.lv,getData()));
       
		try {
			String id = extra.getString("id");
			mViewPager.setCurrentItem(2);
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", id));

			String param = URLEncodedUtils.format(params, "UTF-8");
			String baseUrl = "http://citsm.sinaapp.com/getpush.php";
			HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

			HttpClient httpClient = new DefaultHttpClient();

			try {
				HttpResponse response = httpClient.execute(getMethod); // 发起GET请求
				TextView tv = (TextView) view3.findViewById(R.id.txtView);
				tv.setText(Html.fromHtml(EntityUtils.toString(response.getEntity(), "utf-8"), imgGetter, null));
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {

		}

	}
	 private class MyLocationListener implements LocationListener{
	        
	        public void onLocationChanged(Location location) {
	            location.getAccuracy();//精确度
	            String  latitude = location.getLatitude()+"";//经度
	            String longitude = location.getLongitude()+"";//纬度
	            Editor editor = sp.edit();
	            editor.putString("lastloaction", latitude + "-" + longitude);
	            editor.commit();
	            
	        }
	        
	        
	        public void onStatusChanged(String provider, int status, Bundle extras) {
	            
	        }
	        
	        
	        
	        public void onProviderEnabled(String provider) {
	            
	        }

	        
	        
	        public void onProviderDisabled(String provider) {
	            
	        }
	        
	    }
	public void getList()
	{
		for(int i=0;i<100;i++){
			HashMap<String,Object> map1 =new HashMap<String,Object>();
			map1.put("img", R.drawable.ic_launcher);
			map1.put("title", "中国银行"+i);
			map1.put("info",  12.5+i);
			list.add(map1);
		}
;
	}
	 private List<Map<String, Object>> getData() {
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	 
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("title", "千杯不醉");
	        map.put("info", "做人要谦卑，因为谦卑不醉");
	        map.put("img", R.drawable.ic_launcher);
	        list.add(map);
	        map = new HashMap<String, Object>();
	        map.put("title", "人生际遇");
	        map.put("info", "很多人闯入你的生活知识为了给你上一课，然后转身离开");
	        map.put("img", R.drawable.ic_launcher);
	        list.add(map);
	        map = new HashMap<String, Object>();
	        map.put("title", "安卓开发");
	        map.put("info", "期盼许久你的归来");
	        map.put("img", R.drawable.ic_launcher);
	        list.add(map);
	        return list;
	    }
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;
		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}
		public void finishUpdate(View arg0) {
		}
		public int getCount() {
			return mListViews.size();
		}
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}
		public Parcelable saveState() {
			return null;
		}
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + bmpW;
		int two = one * 2;
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
				case 0:
					if (currIndex == 1) {
						animation = new TranslateAnimation(one, 0, 0, 0);
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, 0, 0, 0);
					}
					break;
				case 1:
					if (currIndex == 0) {
						animation = new TranslateAnimation(offset, one, 0, 0);
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, one, 0, 0);
					}
					break;
				case 2:
					if (currIndex == 0) {
						animation = new TranslateAnimation(offset, two, 0, 0);
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, two, 0, 0);
					}
					break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	private void initGPRS(){
		
		 manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		 List<String> providers = manager.getAllProviders();
		 Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精度要求：ACCURACY_FINE(高)ACCURACY_COARSE(低)
        criteria.setCostAllowed(true);//允许产生开销
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//消耗大的话，获取的频率高
        criteria.setSpeedRequired(true);//手机位置移动
        criteria.setAltitudeRequired(true);//海拔
        
        String bestProvider = manager.getBestProvider(criteria, true);
       
        myLocationListener = new MyLocationListener();
        manager.requestLocationUpdates(bestProvider,1000,5, myLocationListener);
        
      
		
	}
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}
	public void sendPush(String str){
		
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		
		String tagName = sp.getString("tagName", "");

		String[] sArray = tagName.split(",");
		Log.i(TAG,sArray[sArray.length-1]);
		params.add(new BasicNameValuePair("push", sArray[sArray.length-1]));
		
	
		 
		params.add(new BasicNameValuePair("msg", str));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String baseUrl = "http://citsm.sinaapp.com/sg.php";
		HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

		HttpClient httpClient = new DefaultHttpClient();

		try {
			httpClient.execute(getMethod); 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendWeibo(){
		
		String token = sp.getString("token","");
		String expires_in = sp.getString("expires_in","");
		MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
		
		AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);


		StatusesAPI api = new StatusesAPI(MainActivity.accessToken);
		EditText textwibo = (EditText)view3.findViewById(R.id.editText1);
		
		api.update(textwibo.getText().toString(), "", "", new RequestListener() {
			public void onComplete(String arg0) {
				Log.i(TAG, arg0);
				sendPush("发送成功 ");
			}
			public void onError(WeiboException arg0) {
				
			}
			public void onIOException(IOException arg0) {
			
			}
		});

	
	}
	private void initSinaWeibo() {
		Log.i(TAG, "initWeibo");
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		mWeibo.authorize(MainActivity.this, new AuthDialogListener());

	}
	public class AuthDialogListener implements WeiboAuthListener {
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
			if (MainActivity.accessToken.isSessionValid()) {
				
				SharedPreferences preferences = getSharedPreferences("mypush", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("token",token);
				editor.putString("expires_in",expires_in);
				editor.commit();
	
				AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);

				TextView mweibo = (TextView) findViewById(R.id.weibo);
				mweibo.setText("token: " + token);
				AccountAPI aca = new AccountAPI(MainActivity.accessToken);
				aca.getUid(new RequestListener() {
					public void onComplete(String arg0) {
						try {
							JSONTokener jsonParser = new JSONTokener(arg0);
							JSONObject person = (JSONObject) jsonParser.nextValue();
							String uid = person.getString("uid");
							UsersAPI ua = new UsersAPI(MainActivity.accessToken);
							ua.show(Long.parseLong(uid), new RequestListener() {
								public void onComplete(String arg0) {
									Log.i(TAG, arg0);
									try {
										JSONTokener jsonParser = new JSONTokener(arg0);

										JSONObject person = (JSONObject) jsonParser.nextValue();
										String strname = person.getString("name");
										Log.i(TAG, strname);
										String[] sArray = ("android,all," + strname).split(",");

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
										editor.putString("tagName", ("android,all," + strname));
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
					}
					public void onIOException(IOException arg0) {
					}
				});
	
			}
		}

		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private void initView() {
		TextView mImei = (TextView) view1.findViewById(R.id.tv_imei);
		String udid = JPushInterface.getUdid(getApplicationContext());
		if (null != udid)
			mImei.setText("IMEI: " + udid);

		TextView mAppKey = (TextView) view1.findViewById(R.id.tv_appkey);
		String appKey = ExampleUtil.getAppKey(getApplicationContext());
		if (null == appKey)
			appKey = "AppKey异常";
		mAppKey.setText("AppKey: " + appKey);

		String packageName = getPackageName();
		TextView mPackage = (TextView) view1.findViewById(R.id.tv_package);
		mPackage.setText("PackageName: " + packageName);

		String versionName = ExampleUtil.GetVersion(getApplicationContext());
		TextView mVersion = (TextView) view1.findViewById(R.id.tv_version);
		mVersion.setText("Version: " + versionName);

		SharedPreferences preferences = getSharedPreferences("mypush", MODE_PRIVATE);

		String tagName = preferences.getString("tagName", "");
		TextView mTagNmae = (TextView) view1.findViewById(R.id.tag_name);
		mTagNmae.setText("tagName: " + tagName);

		mInit = (Button) view1.findViewById(R.id.init);
		mInit.setOnClickListener(this);

		// mStopPush = (Button)findViewById(R.id.stopPush);
		// mStopPush.setOnClickListener(this);

		mResumePush = (Button) view1.findViewById(R.id.resumePush);
		mResumePush.setOnClickListener(this);
		
		
	
		
		

	}

	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.init:
				init();
				break;
			// case R.id.setting:
			// Intent intent = new Intent(MainActivity.this, PushSetActivity.class);
			// startActivity(intent);
			// break;
			//
			case R.id.resumePush:
				JPushInterface.resumePush(getApplicationContext());
				break;
		}
	}
	private void init() {
		JPushInterface.init(getApplicationContext());
	}
}