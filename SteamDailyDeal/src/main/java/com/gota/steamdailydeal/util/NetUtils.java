package com.gota.steamdailydeal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gota.steamdailydeal.App;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Gota on 2014/6/1.
 * Email: G.tianxiang@gmail.com
 */
public class NetUtils {

    public static boolean isNetAvailable() {
        ConnectivityManager manager = (ConnectivityManager) App.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void loadImageToCache(String url) {
        if (App.cache.isCached(url)) return;

        InputStream in = null;
        try {
            Log.d(App.TAG, "Start download image: " + url);
            URLConnection connection = new URL(url).openConnection();
            in = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            Log.d(App.TAG, "Add image to cache!" + url);
            App.cache.putBitmap(url, bitmap);
        } catch (IOException e) {
            Log.e(App.TAG, "Error on download image!", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

}
