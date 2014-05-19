package com.gota.steamdailydeal;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;


public class ConfigurationActivity extends ActionBarActivity {

    TimePicker tp;

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        tp = (TimePicker) findViewById(R.id.tp_refresh_time);
        tp.setCurrentHour(1);
        tp.setCurrentMinute(0);

        initAppWidgetId();
    }

    public void btnOk(View view) {
        int hour = tp.getCurrentHour();
        int minute = tp.getCurrentMinute();

        App.prefs.edit()
            .putInt(Pref.REFRESH_HOUER, hour)
            .putInt(Pref.REFRESH_MINITUE, minute)
            .commit();

        App.instance.setupAlarm();

        Intent refreshIntent = new Intent(this, DailyDealWidget.class);
        refreshIntent.setAction(DailyDealWidget.ACTION_REFRESH);
        refreshIntent.putExtra(DailyDealWidget.KEY_WIDGET_ID, mAppWidgetId);
        refreshIntent.putExtra(DailyDealWidget.KEY_NEED_RETRY, true);
        refreshIntent.putExtra(DailyDealWidget.KEY_FORCE_REFRESH, true);
        sendBroadcast(refreshIntent);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void initAppWidgetId() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }
}
