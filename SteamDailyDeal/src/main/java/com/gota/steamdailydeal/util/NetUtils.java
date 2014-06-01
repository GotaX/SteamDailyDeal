package com.gota.steamdailydeal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Gota on 2014/6/1.
 * Email: G.tianxiang@gmail.com
 */
public class NetUtils {

    public static void loadNetImage(RemoteViews views, String url) {
        Bitmap bitmap = App.cache.getBitmap(url);
        if (bitmap == null) {
            views.setImageViewResource(R.id.img_header, R.drawable.not_found);
        } else {
            views.setImageViewBitmap(R.id.img_header, bitmap);
        }
    }

    public static void loadImageToCache(final CountDownLatch latch, final String url) {
        if (App.cache.isCached(url)) {
            Log.d(App.TAG, "Image cached: " + url);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(App.TAG, "Start download image: " + url);
                    URLConnection connection = new URL(url).openConnection();
                    InputStream in = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    in.close();

                    Log.d(App.TAG, "Add image to cache!" + url);
                    App.cache.putBitmap(url, bitmap);
                } catch (IOException e) {
                    Log.e(App.TAG, "Error on download image!", e);
                } finally {
                    latch.countDown();
                }
            }
        }).start();
    }

}
