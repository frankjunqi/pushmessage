package com.jj.waterfall;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;

/***
 * 图片缓存
 * 
 * (单利模式)
 * 
 * @author zhangjia
 * 
 */
public class BitmapCache {

	static private BitmapCache cache;
	// 软引用
	private HashMap<String, SoftReference<Bitmap>> imageCache;

	public BitmapCache() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;

	}

	/***
	 * 获取缓存图片
	 * 
	 * @param key
	 *            image name
	 * @return
	 */
	public Bitmap getBitmap(String key) {
		if (imageCache.containsKey(key)) {
			SoftReference<Bitmap> reference = imageCache.get(key);
			Bitmap bitmap = reference.get();
			if (bitmap != null)
				return bitmap;
		}
		return null;

	}

	/***
	 * 将图片添加到软引用中
	 * 
	 * @param bitmap
	 * @param key
	 */
	public void putSoftReference(Bitmap bitmap, String key) {
		imageCache.put(key, new SoftReference<Bitmap>(bitmap));

	}

}
