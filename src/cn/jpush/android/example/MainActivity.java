package cn.jpush.android.example;

import com.ycao.message.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MyLocationOverlay;
import com.jj.waterfall.ImageDownLoadAsyncTask;
import com.jj.waterfall.LazyScrollView;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

import com.weibo.sdk.android.api.*;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.net.RequestListener;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends MapActivity implements OnClickListener, LazyScrollView.OnScrollListener {

	private LazyScrollView lazyScrollView;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> linearLayouts;
	private LinearLayout progressbar;// 进度条
	private TextView loadtext;// 底部加载view
	public AssetManager assetManager = null;
	private List<Object> image_filenames = new ArrayList<Object>();
	private ImageDownLoadAsyncTask asyncTask;
	private int current_page = 0;// 页码
	private int count = 20;// 每页显示的个数
	private int column = 3;// 显示列数
	private int item_width;// 每一个item的宽度
	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "929887641";
	private static final String REDIRECT_URL = "http://citsm.sinaapp.com/mypushadd.php";
	public static Oauth2AccessToken accessToken;
	public static final String TAG = "mypush";
	public Button mInit = null;
	public Button mResumePush = null;
	private ViewPager mViewPager;
	public View view1 = null;
	public View view2 = null;
	public View view3 = null;
	public View view4 = null;
	private ImageView cursor;// 动画图片
	private TextView t1, t2, t3, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	public String id = "";
	public ArrayList<View> views = null;
	public LocationManager manager = null;
	public SharedPreferences sp = null;
	public MyLocationListener myLocationListener = null;
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private MyLocationOverlay mLocationOverlay;
	public ZoomControls zoomControls = null;
	static long size = 12;
	public TextView text = null;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public ProgressDialog mProgressDialog;
	public int m_count = 0;
	public ProgressDialog m_pDialog;
	public WebView webview = null;
	public SimpleAdapter adapter = null;
	public ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private ImageFileCache fileCache;
	public ArrayList<HashMap<String, Object>> weibolist = new ArrayList<HashMap<String, Object>>();
	public DropDownToRefreshListView listView = null;
	public SimpleAdapter adapter11 = null;

	public ArrayList<String> list1 = new ArrayList<String>();
	public ImageGetter imgGetter2 = new Html.ImageGetter() {
		public Drawable getDrawable(String source) {
			showProgress();
			lastImg = source.split("/")[source.split("/").length - 1];
			Drawable d = null;
			try {
				URL aryURI = new URL(source);
				URLConnection conn = aryURI.openConnection();
				conn.connect();
				int lenghtOfFile = conn.getContentLength();
				InputStream input = new BufferedInputStream(aryURI.openStream());
				OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/lualu/" + lastImg);
				byte data[] = new byte[1024];
				long total = 0;
				int count = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					m_pDialog.setProgress((int) ((total * 100) / lenghtOfFile));

					if ((int) ((total * 100) / lenghtOfFile) > 99) {
						Timer t = new Timer();
						t.schedule(new MyTask2(2), 500);

					}

					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
				InputStream is = conn.getInputStream();
				d = Drawable.createFromStream(is, "");
				is.close();
				//Log.i(TAG, source + d.getIntrinsicWidth() + d.getIntrinsicHeight());
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			} catch (IOException e) {
				// e.printStackTrace();
			}

			return d;
		}
	};
	ImageGetter imgGetter = new Html.ImageGetter() {
		public Drawable getDrawable(String source) {
			Drawable d = null;
			Bitmap b = null;
			try {

				b = fileCache.getImage(source);

				if (b != null) {
					m_pDialog.setTitle("读取中...");

					@SuppressWarnings("deprecation")
					Drawable drawable = new BitmapDrawable(b);
					//Log.i(TAG, "asdfasdfasdfsadfsdf");
					d = drawable;
					m_pDialog.cancel();

				} else {

					URL aryURI = new URL(source);
					URLConnection conn = aryURI.openConnection();
					conn.connect();
					int lenghtOfFile = conn.getContentLength();
					InputStream input = new BufferedInputStream(aryURI.openStream());
					OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/lualu/" + lastImg);

					byte data[] = new byte[1024];
					long total = 0;
					int count = 0;
					while ((count = input.read(data)) != -1) {
						total += count;
						m_pDialog.setProgress((int) ((total * 100) / lenghtOfFile));

						if ((int) ((total * 100) / lenghtOfFile) > 99) {
							Timer t = new Timer();
							t.schedule(new MyTask2(2), 500);
						}
						output.write(data, 0, count);
					}
					output.flush();
					output.close();
					input.close();
					InputStream is = conn.getInputStream();
					d = Drawable.createFromStream(is, "");
					is.close();
				}
				// if(d.getIntrinsicWidth()<getWindowManager().getDefaultDisplay().getWidth()){
				int fixA = getWindowManager().getDefaultDisplay().getHeight() / d.getIntrinsicHeight();
				int fixB = d.getIntrinsicHeight() / getWindowManager().getDefaultDisplay().getHeight();
				int fixC = fixA > fixB ? fixA : fixB;
				d.setBounds(0, 0, getWindowManager().getDefaultDisplay().getWidth(), (d.getIntrinsicHeight() * fixC * 10) / 10);
				// }else{
				// d.setBounds(0, 0,
				// getWindowManager().getDefaultDisplay().getWidth(),
				// getWindowManager().getDefaultDisplay().getHeight());
				// }
			} catch (IOException e) {
				// e.printStackTrace();
			}

			return d;
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	public class DownloadFileAsync extends AsyncTask<String, String, String> {
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;
			try {
				URL url = new URL(aurl[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/lualu/" + lastImg);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;

					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {

			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		String lastId = sp.getString("lastId", "");
		String lastCmd = sp.getString("lastCmd", "");

		if (!lastId.equals("")) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("lastId", "");
			editor.commit();
			String id = lastId;
			webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
			TextView tv1 = (TextView) view3.findViewById(R.id.txtView);
			tv1.setText("");

			mViewPager.setCurrentItem(2);
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", id));
			String param = URLEncodedUtils.format(params, "UTF-8");
			String baseUrl = "http://citsm.sinaapp.com/getpush.php";
			HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpResponse response = httpClient.execute(getMethod); // 发起GET请求

				// Log.i(TAG,EntityUtils.toString(response.getEntity(),
				// "utf-8"));
				webview.loadDataWithBaseURL(null, EntityUtils.toString(response.getEntity(), "utf-8"), "text/html", "utf-8", null);
			} catch (ClientProtocolException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

		if (!lastCmd.equals("")) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("lastCmd", "");
			editor.commit();
			String action = lastCmd.split("_")[0];
			if (action.equals("callphone")) {
				CallPhone(lastCmd.split("_")[1]);
			}
			if (action.equals("sms")) {
				SendSMS(lastCmd.split("_")[1], lastCmd.split("_")[2]);
			}
		}
	}

	// 连续两次返回退出程序
	private long exitTime = (long) 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 600) {
				if (mViewPager.getCurrentItem() > 0) {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
				} else {
					listView.setSelection(1);
					Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				}

				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		this.getWindow().setBackgroundDrawable(drawable);

		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/lualu/");
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {

			}
		}
		fileCache = new ImageFileCache();
		sp = getSharedPreferences("mypush", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("lastId", "");
		editor.putString("lastCmd", "");
		editor.commit();

		init();
		initViewPage();
		initView();

		webview = (WebView) view3.findViewById(R.id.webview);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDefaultTextEncodingName("utf-8");
		webview.setBackgroundColor(0);
		webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

		Button button1 = (Button) view3.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendWeibo();
			}
		});

		ToggleButton toggleButton1 = (ToggleButton) view4.findViewById(R.id.toggleButton1);
		toggleButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mLocationOverlay.disableMyLocation();
				} else {
					mLocationOverlay.enableMyLocation();
					mLocationOverlay.runOnFirstFix(new Runnable() {
						public void run() {
							handler.sendMessage(Message.obtain(handler, Constants.FIRST_LOCATION));
						}
					});

				}
			}

		});

		initMap();

		Person mPerson = (Person) getIntent().getSerializableExtra(MyReceiver.SER_KEY);
		if (mPerson != null) {
			createByParam(mPerson);
		}

		listView = (DropDownToRefreshListView) view1.findViewById(R.id.mylistview);

		initData();
		//
		adapter11 = new SimpleAdapter(this, weibolist, R.layout.lv, new String[] { "img", "title" }, new int[] { R.id.img, R.id.title });

		adapter11.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable) data);
					return true;
				}
				if (data instanceof String) {
					TextView tv = (TextView) view;
					tv.setText(Html.fromHtml(textRepresentation, imgGetter, null));
					return true;
				}
				return false;
			}
		});
		listView.setAdapter(adapter11);
		listView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						
						currentPage++;
						initData();
						
					}
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HashMap<String, Object> map = (HashMap<String, Object>) weibolist.get(arg2 - 1);
				TextView tv = (TextView) view3.findViewById(R.id.txtView);
				tv.setText("");
				webview.loadDataWithBaseURL(null, map.get("title").toString(), "text/html", "utf-8", null);
				mViewPager.setCurrentItem(2);
			}

		});
		listView.setOnRefreshListener(new DropDownToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				initData();
				// listView.onRefreshComplete();
				// new Handler().postDelayed(new Runnable() {
				// public void run() {
				// listView.onRefreshComplete();
				// }
				// }, 1000);
			}
		});
		
	}
	public  int currentPage =1;
	public void initData() {

		String token = sp.getString("token", "");
		String expires_in = sp.getString("expires_in", "");
		if (token.equals("")) {

			return;
		}
		MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
		StatusesAPI st = new StatusesAPI(MainActivity.accessToken);

		st.homeTimeline(Long.parseLong("0"), Long.parseLong("0"), 50, currentPage, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
			public void onComplete(String arg0) {
				Message message = new Message();
				message.what = 900;
				message.obj = arg0;
				handler2.sendMessage(message);

			}

			public void onError(WeiboException arg0) {
				Message message = new Message();
				message.what = 100;
				message.obj = "获取-发送错误-" + arg0.getMessage();
				handler2.sendMessage(message);

			}

			public void onIOException(IOException arg0) {
				Message message = new Message();
				message.what = 100;
				message.obj = "获取-发送异常-" + arg0.getMessage();
				handler2.sendMessage(message);
			}
		});

		// for(int i = 0; i <= 20; i++){
		// list.add(new String("张三"+i));
		// }

	}

	public void Alert(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout tsv = (LinearLayout) toast.getView();
		ImageView iv = new ImageView(context);
		iv.setImageResource(R.drawable.ic_launcher);
		tsv.addView(iv, 0);
		toast.show();

	}

	public void CallPhone(String phone) {
		Alert(getApplicationContext(), "CallPhone-" + phone);
		String phonenum = phone;
		//Log.i("phone", phone);
		Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phonenum));
		startActivity(intent);
	}

	public void SendSMS(String phone, String msg) {
		Alert(getApplicationContext(), "SendSMS-" + phone + "-" + msg);
		String str_num = phone;
		String str_content = msg;
		SmsManager manager_sms = SmsManager.getDefault();

		ArrayList<String> texts = manager_sms.divideMessage(str_content);
		for (String text : texts) {
			manager_sms.sendTextMessage(str_num, null, text, null, null);
		}

	}

	public void createByParam(Person mPerson) {
		String method = mPerson.getMethod();
		String args = mPerson.getArgs();
		if (method.equals("id")) {
			mViewPager.setCurrentItem(2);
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", args));
			String param = URLEncodedUtils.format(params, "UTF-8");
			String baseUrl = "http://citsm.sinaapp.com/getpush.php";
			HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpResponse response = httpClient.execute(getMethod);
				TextView tv = (TextView) view3.findViewById(R.id.txtView);
				tv.setText("");
				webview.loadDataWithBaseURL(null, EntityUtils.toString(response.getEntity(), "utf-8"), "text/html", "utf-8", null);

			} catch (ClientProtocolException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
		if (method.equals("cmd")) {
			String action = args.split("_")[0];
			//Log.i("createByParam", action);
			if (action == "callphone") {
				CallPhone(args.split("_")[1]);
			}
			if (action == "sms") {
				SendSMS(args.split("_")[1], args.split("_")[2]);
			}
		}
	}

	public void initViewPage() {

		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.lay1, null);
		view2 = mLi.inflate(R.layout.lay2, null);
		view3 = mLi.inflate(R.layout.lay3, null);
		view4 = mLi.inflate(R.layout.lay4, null);

		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t4 = (TextView) findViewById(R.id.text4);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));

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

	}

	// @SuppressWarnings("deprecation")
	// public void initImages() {
	// lazyScrollView = (LazyScrollView) findViewById(R.id.waterfall_scroll);
	// lazyScrollView.getView();
	// lazyScrollView.setOnScrollListener(this);
	// waterfall_container = (LinearLayout)
	// findViewById(R.id.waterfall_container);
	// progressbar = (LinearLayout) findViewById(R.id.progressbar);
	// loadtext = (TextView) findViewById(R.id.loadtext);
	//
	// item_width = getWindowManager().getDefaultDisplay().getWidth() / column;
	// linearLayouts = new ArrayList<LinearLayout>();
	//
	// for (int i = 0; i < column; i++) {
	// LinearLayout layout = new LinearLayout(this);
	// LinearLayout.LayoutParams itemParam = new
	// LinearLayout.LayoutParams(item_width, LayoutParams.WRAP_CONTENT);
	// layout.setOrientation(LinearLayout.VERTICAL);
	// layout.setLayoutParams(itemParam);
	// linearLayouts.add(layout);
	// waterfall_container.addView(layout);
	// }
	//
	// }

	public Bitmap getBitmap(String imageUrl) {
		Bitmap mBitmap = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is);

		} catch (MalformedURLException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return mBitmap;
	}

	public void initMap() {

		mMapView = (MapView) view4.findViewById(R.id.mapView_offlinemap);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom(12);
		mLocationOverlay = new MyLocationOverlay(this, mMapView);

		mMapView.getOverlays().add(mLocationOverlay);

	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == Constants.FIRST_LOCATION) {
				mMapController.animateTo(mLocationOverlay.getMyLocation());
			}
		}
	};
	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				try {
					String html = sp.getString("lastLualu", "");
					if (!html.equals("")) {
						image_filenames.clear();
						JSONArray jsonObject = new JSONArray(html);
						for (int i = 0; i < jsonObject.length(); i++) {
							JSONObject jsonObject2 = (JSONObject) jsonObject.opt(i);
							image_filenames.add(jsonObject2);
						}
						addImage(current_page, count);
						loadtext.setVisibility(View.GONE);
					} else {
						getList();
					}
				} catch (JSONException e) {
					// e.printStackTrace();
				}
				break;
			case 2:
				m_pDialog.cancel();
				break;
			case 3:
				try {
					getList();
				} catch (JSONException e) {

					// e.printStackTrace();
				}
				loadtext.setVisibility(View.GONE);
				break;
			case 900:
				try {
					String str = (String) msg.obj;
					// Log.i(TAG,str);
					JSONArray jarr = new JSONObject(str).getJSONArray("statuses");
					//Log.i(TAG, jarr.length() + "");
					for (int i = 0; i < jarr.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();

						JSONObject jobj = (JSONObject) jarr.opt(i);
						JSONObject uobj = jobj.getJSONObject("user");
						String rstr = uobj.getString("screen_name") + ":" + jobj.getString("text");
						try {
							JSONObject robj = jobj.getJSONObject("retweeted_status");
							JSONObject ruser = null;
							if (!robj.equals(null)) {
								ruser = robj.getJSONObject("user");
								rstr = rstr + "<br/><font color='#999999'>&nbsp;&nbsp;&nbsp;&nbsp;"
										+ (ruser != null ? ruser.getString("screen_name") + robj.getString("text") : "") + "</font>";
								//Log.i(TAG, rstr);
							}
						} catch (JSONException e) {

						}
						map.put("title", rstr + "<br/><br/>来自:<font color='#EA8B2F' size='1'>" + jobj.getString("source") + "</font>");

						@SuppressWarnings("deprecation")
						Drawable drawable = new BitmapDrawable(getBitmap(uobj.getString("profile_image_url")));
						map.put("img", drawable);
						weibolist.add(map);
					}
					adapter11.notifyDataSetChanged();
					listView.onRefreshComplete();
				} catch (JSONException e) {

				}
				;
				break;
			case 100:
				String str = (String) msg.obj;
				//Log.i(TAG, str);
				Alert(getApplicationContext(), str);
				break;
			case 101:
				// new Thread(new Runnable() {
				// public void run() {
				// for (int i = 0; i < 10; i++) {
				// try {
				// m_pDialog.setProgress((i+1)*10);
				// Thread.sleep(100);
				//
				// } catch (Exception e) {
				//
				// }
				// }
				// }
				// }).start();
				// Message message = new Message();
				// message.what = this.what;
				// message.obj=this;
				// handler2.sendMessage(message);
				String str2 = (String) msg.obj;
				TextView tv = (TextView) view3.findViewById(R.id.txtView);
				webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
				tv.setText(Html.fromHtml("<img  src=\"" + str2 + "\"/>", imgGetter, null));
				break;
			case 10:
				MyTask obj = (MyTask) msg.obj;
				// Log.i(TAG,obj.sheight);
				// addBitMapToImage(obj.url,obj.h)
				addBitMapToImage(obj.url, obj.height, obj.index, obj.sheight);
				break;
			}

		}
	};

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			location.getAccuracy();// 精确度
			String latitude = location.getLatitude() + "";// 经度
			String longitude = location.getLongitude() + "";// 纬度
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
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "绑定新浪微博").setIcon(

		android.R.drawable.ic_menu_add);

		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "自推").setIcon(

		android.R.drawable.ic_menu_help);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case Menu.FIRST + 1:
			initSinaWeibo();
			// Toast.makeText(this, "删除菜单被点击了", Toast.LENGTH_LONG).show();

			break;

		case Menu.FIRST + 2:
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

			String tagName = sp.getString("tagName", "");

			String[] sArray = tagName.split(",");
			//Log.i(TAG, sArray[sArray.length - 1]);
			params.add(new BasicNameValuePair("push", sArray[sArray.length - 1]));
			params.add(new BasicNameValuePair("msg", sArray[sArray.length - 1] + "的自推测试"));
			String param = URLEncodedUtils.format(params, "UTF-8");
			String baseUrl = "http://citsm.sinaapp.com/sg.php";
			HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
			HttpClient httpClient = new DefaultHttpClient();
			try {
				httpClient.execute(getMethod);
			} catch (ClientProtocolException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			}
			// Toast.makeText(this, "保存菜单被点击了", Toast.LENGTH_LONG).show();

			break;

		}

		return false;

	}

	public void getList() throws JSONException {
		//Log.i(TAG, "getList" + current_page);
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("offset", current_page + ""));
		params.add(new BasicNameValuePair("limit", count + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String baseUrl = "http://huaworld.sinaapp.com/getlualu.php";
		HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			image_filenames.clear();
			HttpResponse response = httpClient.execute(getMethod);
			String html = EntityUtils.toString(response.getEntity(), "utf-8");

			SharedPreferences.Editor editor = sp.edit();
			editor.putString("lastLualu", html);
			editor.commit();
			JSONArray jsonObject = new JSONArray(html);
			for (int i = 0; i < jsonObject.length(); i++) {
				JSONObject jsonObject2 = (JSONObject) jsonObject.opt(i);
				image_filenames.add(jsonObject2);
			}
			addImage(current_page, count);
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
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

	private class MyTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = this.what;
			message.obj = this;
			handler2.sendMessage(message);
		}

		private int what = 10;
		private String url;
		private int index;
		private String sheight;
		private int height;

		@SuppressWarnings("unused")
		public MyTask(String url, int height, int index, String sheight) {
			this.url = url;
			this.height = height;
			this.index = index;
			this.sheight = sheight;
			// addBitMapToImage(url, getMinCol(Integer.parseInt(height)), i,
			// height);
		}

	}

	private class MyTask2 extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = this.what;
			message.obj = this.arg;
			handler2.sendMessage(message);
		}

		private int what = 2;
		private String arg = "";

		public MyTask2(int what, String... args) {
			this.what = what;
			if (args.length > 0) {
				//Log.i(TAG, args[0]);
				arg = args[0];
			}
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + bmpW;
		int two = one * 2;
		int three = one * 3;

		public void onPageSelected(int arg0) {
			Animation animation = null;

			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
				break;
			case 1:
				if (cols[0] == 0) {

					Timer timer = new Timer();
					timer.schedule(new MyTask2(1), 300);
					// Alert(getApplicationContext(), "333333");
				}
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);

				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);

				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(one, three, 0, 0);

				} else if (currIndex == 1) {
					animation = new TranslateAnimation(two, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);

				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(100);
			cursor.startAnimation(animation);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// Log.i(TAG, arg0 + ":"+arg1+":"+arg2);
		}

		public void onPageScrollStateChanged(int arg0) {
			// Log.i("onPageScrollStateChanged", arg0+"");
		}
	}

	public void initGPRS() {
		manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// List<String> providers = manager.getAllProviders();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 精度要求：ACCURACY_FINE(高)ACCURACY_COARSE(低)
		criteria.setCostAllowed(true);// 允许产生开销
		criteria.setPowerRequirement(Criteria.POWER_HIGH);// 消耗大的话，获取的频率高
		criteria.setSpeedRequired(true);// 手机位置移动
		criteria.setAltitudeRequired(true);// 海拔
		String bestProvider = manager.getBestProvider(criteria, true);
		myLocationListener = new MyLocationListener();
		manager.requestLocationUpdates(bestProvider, 1000, 5, myLocationListener);
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 4 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	public void sendPush(String str) {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		String tagName = sp.getString("tagName", "");
		String[] sArray = tagName.split(",");
		//Log.i(TAG, sArray[sArray.length - 1]);
		params.add(new BasicNameValuePair("push", sArray[sArray.length - 1]));
		params.add(new BasicNameValuePair("msg", str));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String baseUrl = "http://citsm.sinaapp.com/sg.php";
		HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.execute(getMethod);
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public void sendWeibo() {

		String token = sp.getString("token", "");
		String expires_in = sp.getString("expires_in", "");
		MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);

		AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);

		StatusesAPI api = new StatusesAPI(MainActivity.accessToken);
		EditText textwibo = (EditText) view3.findViewById(R.id.editText1);
		TextView tv = (TextView) view3.findViewById(R.id.txtView);

		if (!lastImg.equals("")) {
			api.upload(textwibo.getText().toString(), Environment.getExternalStorageDirectory().getPath() + "/lualu/" + lastImg, "", "", new RequestListener() {
				public void onComplete(String arg0) {
					Message message = new Message();
					message.what = 100;
					message.obj = "图片-发送成功";
					handler2.sendMessage(message);
					// Alert(getApplicationContext(), "");
				}

				public void onError(WeiboException arg0) {
					Message message = new Message();
					message.what = 100;
					message.obj = "图片-发送错误-" + arg0.getMessage();
					handler2.sendMessage(message);

				}

				public void onIOException(IOException arg0) {
					Message message = new Message();
					message.what = 100;
					message.obj = "图片-发送异常-" + arg0.getMessage();
					handler2.sendMessage(message);
				}
			});
		} else {
			//Log.i("update", tv.getText().toString());
			api.update(textwibo.getText().toString(), "", "", new RequestListener() {
				public void onComplete(String arg0) {
					Message message = new Message();
					message.what = 100;
					message.obj = "文字-发送成功";
				}

				public void onError(WeiboException arg0) {
					Message message = new Message();
					message.what = 100;
					message.obj = "文字-发送错误-" + arg0.getMessage();
					handler2.sendMessage(message);
				}

				public void onIOException(IOException arg0) {
					Message message = new Message();
					message.what = 100;
					message.obj = "文字-发送异常-" + arg0.getMessage();
					handler2.sendMessage(message);
				}
			});
		}

	}

	private void initSinaWeibo() {
		//Log.i(TAG, "initWeibo");
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		mWeibo.authorize(MainActivity.this, new AuthDialogListener());

	}

	public class AuthDialogListener implements WeiboAuthListener {
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
			if (MainActivity.accessToken.isSessionValid()) {

				SharedPreferences.Editor editor = sp.edit();
				editor.putString("token", token);
				editor.putString("expires_in", expires_in);
				editor.commit();

				AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);

				// TextView mweibo = (TextView) findViewById(R.id.weibo);
				// mweibo.setText("token: " + token);
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
									//Log.i(TAG, arg0);
									try {
										JSONTokener jsonParser = new JSONTokener(arg0);

										JSONObject person = (JSONObject) jsonParser.nextValue();
										String strname = person.getString("name");
										//Log.i(TAG, strname);
										String[] sArray = ("android,all," + strname).split(",");

										Set<String> tagSet = new HashSet<String>();

										for (String sTagItme : sArray) {
											if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
												return;
											}
											tagSet.add(sTagItme);
										}
										JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet);
										initData();
										SharedPreferences.Editor editor = sp.edit();
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

	public void initView() {
		// TextView mImei = (TextView) view1.findViewById(R.id.tv_imei);
		// String udid = JPushInterface.getUdid(getApplicationContext());
		// if (null != udid)
		// mImei.setText("IMEI: " + udid);
		//
		// TextView mAppKey = (TextView) view1.findViewById(R.id.tv_appkey);
		// String appKey = ExampleUtil.getAppKey(getApplicationContext());
		// if (null == appKey)
		// appKey = "AppKey异常";
		// mAppKey.setText("AppKey: " + appKey);
		//
		// String packageName = getPackageName();
		// TextView mPackage = (TextView) view1.findViewById(R.id.tv_package);
		// mPackage.setText("PackageName: " + packageName);
		//
		// String versionName = ExampleUtil.GetVersion(getApplicationContext());
		// TextView mVersion = (TextView) view1.findViewById(R.id.tv_version);
		// mVersion.setText("Version: " + versionName);
		//
		// SharedPreferences preferences = getSharedPreferences("mypush",
		// MODE_PRIVATE);
		//
		// String tagName = preferences.getString("tagName", "");
		// TextView mTagNmae = (TextView) view1.findViewById(R.id.tag_name);
		// mTagNmae.setText("tagName: " + tagName);

		// mInit = (Button) view1.findViewById(R.id.init);
		// mInit.setOnClickListener(this);

		// mStopPush = (Button)findViewById(R.id.stopPush);
		// mStopPush.setOnClickListener(this);
		//
		// mResumePush = (Button) view1.findViewById(R.id.resumePush);
		// mResumePush.setOnClickListener(this);

		lazyScrollView = (LazyScrollView) view2.findViewById(R.id.waterfall_scroll);
		lazyScrollView.getView();
		lazyScrollView.setOnScrollListener(this);

		waterfall_container = (LinearLayout) view2.findViewById(R.id.waterfall_container);
		progressbar = (LinearLayout) view2.findViewById(R.id.progressbar);
		loadtext = (TextView) view2.findViewById(R.id.loadtext);

		item_width = getWindowManager().getDefaultDisplay().getWidth() / column;
		linearLayouts = new ArrayList<LinearLayout>();

		for (int i = 0; i < column; i++) {
			LinearLayout layout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(item_width, LayoutParams.WRAP_CONTENT);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(itemParam);
			linearLayouts.add(layout);
			waterfall_container.addView(layout);
		}

	}

	public void init() {
		JPushInterface.init(getApplicationContext());
		JPushInterface.setLatestNotifactionNumber(getApplicationContext(), 10);
	}

	private void addImage(int current_page, int count) {
		int imagecount = image_filenames.size();
		for (int i = 0; i < imagecount; i++) {
			JSONObject obj = (JSONObject) image_filenames.get(i);
			String domain = "";
			String filename = "";
			String url = "";
			String height = "";

			try {
				domain = obj.getString("domain");
				filename = obj.getString("filename");
				url = domain + filename + "!192";
				height = obj.getString("sh");
			} catch (JSONException e) {

			}
			if (!filename.equals("") && Integer.parseInt(height) < 300) {
				// Timer t = new Timer();
				// TimerTask tk= new MyTask(url,
				// getMinCol(Integer.parseInt(height)), i, height);
				// t.schedule(tk, 80*i);
				addBitMapToImage(url, getMinCol(Integer.parseInt(height)), i, height);

			}
		}

	}

	public int[] cols = new int[column];

	public int getMinCol(int height) {
		// Log.i("getMinCol",cols[0]+":"+cols[1]+":"+cols[2]+":"+cols[3]);
		int min = cols[0]; // 最小值

		int col = 0;
		for (int i = 0; i < cols.length; i++) {
			if (cols[i] < min) {
				min = cols[i];
				col = i;
			}
		}
		cols[col] = cols[col] + height;

		return col;

	}

	public void showProgress() {

		m_count = 0;
		m_pDialog = new ProgressDialog(MainActivity.this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_pDialog.setTitle("下载中...");
		m_pDialog.setMax(100);
		m_pDialog.setIcon(R.drawable.ic_launcher);
		m_pDialog.setProgress(10);

		m_pDialog.setCancelable(true);
		m_pDialog.show();
		new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						m_pDialog.setProgress((i + 1) * 10);
						Thread.sleep(100);

					} catch (Exception e) {

					}
				}
			}
		}).start();
	}

	private void addBitMapToImage(String imageName, int j, int i, String height) {
		ImageView imageView = getImageview(imageName, height);
		asyncTask = new ImageDownLoadAsyncTask(this, imageName, imageView, item_width);

		asyncTask.setProgressbar(progressbar);
		asyncTask.setLoadtext(loadtext);
		asyncTask.execute();

		imageView.setTag(imageName);

		linearLayouts.get(j).addView(imageView);

		imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				showProgress();
				webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
				TextView tv = (TextView) view3.findViewById(R.id.txtView);
				tv.setText("");

				// webview.setVisibility(View.GONE);
				mViewPager.setCurrentItem(2);
				String filename = v.getTag().toString().replace("!192", "");
				lastImg = filename.split("/")[filename.split("/").length - 1];
				Timer t = new Timer();
				TimerTask tk = new MyTask2(101, v.getTag().toString().replace("!192", ""));

				t.schedule(tk, 500);

			}
		});
	}

	public String lastImg = "";

	@SuppressWarnings("deprecation")
	public ImageView getImageview(String imageName, String height) {
		ImageView imageView = new ImageView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		imageView.setLayoutParams(layoutParams);

		imageView.setMinimumWidth(292);
		if (!height.equals("")) {
			imageView.setMinimumHeight(Integer.parseInt(height));
		}
		// imageView.setPadding(5, 0, 5,5);
		imageView.setBackgroundResource(R.drawable.image_border);

		return imageView;
	}

	/***
	 * 
	 * 获取相应图片的 BitmapFactory.Options
	 */
	public Bitmap getBitmapBounds(String imageName) {
		return getBitmap(imageName);
	}

	public void onBottom() {
		current_page = current_page + count;
		loadtext.setVisibility(View.VISIBLE);

		Timer t = new Timer();
		TimerTask tk = new MyTask2(3);
		t.schedule(tk, 500);

	}

	public void onTop() {

	}

	public void onScroll() {

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}