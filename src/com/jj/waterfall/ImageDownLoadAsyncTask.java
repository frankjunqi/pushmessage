package com.jj.waterfall;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jpush.android.example.ImageFileCache;
import cn.jpush.android.example.ImageGetForHttp;
import cn.jpush.android.example.ImageMemoryCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 异步下载图片
 * 
 * @author sy
 * 
 */
public class ImageDownLoadAsyncTask extends AsyncTask<Void, Void, Bitmap> {
	private ExecutorService executorService; // 固定五个线程来
    private ImageMemoryCache memoryCache;// 内存缓存
    private ImageFileCache fileCache;// 文件缓存
    private Map<String, ImageView> taskMap;// 存放任务
	private String imagePath;
	private ImageView imageView;
	
	private int Image_width;// 显示图片的宽度

	private LinearLayout progressbar;
	private TextView loadtext;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param imagePath
	 * @param imageView
	 */
	public ImageDownLoadAsyncTask(Context context, String imagePath, ImageView imageView, int Image_width) {
		this.imagePath = imagePath;
		this.imageView = imageView;

		this.Image_width = Image_width;
		//executorService = Executors.newFixedThreadPool(20);

        memoryCache = new ImageMemoryCache();
        fileCache = new ImageFileCache();
        taskMap = new HashMap<String, ImageView>();
		
	}

	public void setLoadtext(TextView loadtext) {
		this.loadtext = loadtext;
	}

	public void setProgressbar(LinearLayout progressbar) {
		this.progressbar = progressbar;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {

		return getBitmap(imagePath);
		
	}

	@Override
	protected void onPostExecute(Bitmap drawable) {
		
		super.onPostExecute(drawable);
		
		if (drawable != null) {
			LayoutParams layoutParams = imageView.getLayoutParams();
			int height = drawable.getHeight();
			int width = drawable.getWidth();
			layoutParams.height = (height * Image_width) / width;
			imageView.setLayoutParams(layoutParams);
			imageView.setImageBitmap(drawable);
			imageView.setBackgroundResource(0);
			
			if(drawable.isRecycled()){				
				drawable.recycle();  				
			}
			drawable=null;		
		}
		if (progressbar.isShown() || loadtext.isShown()) {
			progressbar.setVisibility(View.GONE);		
		}
	}
	public Bitmap getBitmap(String imageUrl) {
		
		Bitmap result;
        result = memoryCache.getBitmapFromCache(imageUrl);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(imageUrl);
            if (result == null) {
                // 从网络获取
                result = ImageGetForHttp.downloadBitmap(imageUrl);
//                if (result != null) {
//                    memoryCache.addBitmapToCache(imageUrl, result);
//                    fileCache.saveBmpToSd(result, imageUrl);                    
//                }
            } else {
                // 添加到内存缓存
                //memoryCache.addBitmapToCache(imageUrl, result);
            }
        }
        return result;
        
		/*Bitmap mBitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			options.inPreferredConfig = Bitmap.Config.ARGB_4444;
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is, null, options);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
		*/
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		if (!loadtext.isShown()) {
//			loadtext.setVisibility(View.VISIBLE);
//		}

	}
}