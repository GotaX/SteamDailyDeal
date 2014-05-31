package com.gota.steamdailydeal.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.gota.steamdailydeal.R;
import com.gota.steamdailydeal.entity.Deal;
import com.gota.steamdailydeal.util.JSONUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestActivity extends Activity {

    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tvContent = (TextView) findViewById(R.id.tv_content);
        tvContent.setMovementMethod(new ScrollingMovementMethod());

        AssetManager am = getAssets();
        InputStream in = null;
        try {
            in = am.open("wednesday.json");
            List<Deal> deals = JSONUtils.parseDeal(in);
            tvContent.setText(deals.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            assert in != null;
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
