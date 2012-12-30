package com.jj.waterfall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
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
			loadtext.setVisibility(View.GONE);
		}

	}
	public Bitmap getBitmap(String imageUrl) {
		Bitmap mBitmap = null;
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
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (!loadtext.isShown()) {
			loadtext.setVisibility(View.VISIBLE);
		}

	}
}