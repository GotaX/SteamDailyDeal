package com.gota.steamdailydeal.volley;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.gota.steamdailydeal.App;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Gota on 2014/5/17.
 * Email: G.tianxiang@gmail.com
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> mCache;
    private DiskBasedCache mDiskCache;

    public BitmapCache() {
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mDiskCache = new DiskBasedCache(App.instance.getCacheDir(), maxSize * 3);
        mDiskCache.initialize();
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = mCache.get(url);
        if (bitmap != null) return bitmap;

        Cache.Entry entry = mDiskCache.get(url);
        if (entry != null) {
            return BitmapFactory.decodeByteArray(entry.data, 0, entry.data.length);
        }

        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean ok = bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (ok) {
            Cache.Entry entry = new Cache.Entry();
            entry.data = baos.toByteArray();
            mDiskCache.put(url, entry);
        }
    }

    public boolean isCached(String url) {
        File file = mDiskCache.getFileForKey(url);
        return file.exists();
    }
}
