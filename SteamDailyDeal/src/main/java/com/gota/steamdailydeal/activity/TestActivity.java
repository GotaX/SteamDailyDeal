package com.gota.steamdailydeal.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.R;
import com.gota.steamdailydeal.WorkService;
import com.gota.steamdailydeal.data.DataProvider;
import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.util.MyTextUtils;

public class TestActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView lv;
    SimpleCursorAdapter mAdapter;

    private static final String[] from = {
            Tables.TDeals.NAME, Tables.TDeals.CATEGORY
    };
    private static final int[] to = {
            R.id.tv_name, R.id.tv_category
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        lv = (ListView) findViewById(android.R.id.list);
        mAdapter = new SimpleCursorAdapter(this,
                R.layout.deal_item, null, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mAdapter.getViewBinder();
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 1) {
                    int category = cursor.getInt(columnIndex);
                    String strCategory = MyTextUtils.getCategory(category);
                    ((TextView)view).setText(strCategory);
                    return true;
                }
                return false;
            }
        });
        lv.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(App.TAG, "Loader create!");
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, DataProvider.PATH_DEAL);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.d(App.TAG, "Loader finished!");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(App.TAG, "Loader reset!");
        mAdapter.swapCursor(null);
    }

    public void btnTest(View view) {
        Log.d(App.TAG, "Button pressed");
//        WorkService.startActionUpdateData(this);
        WorkService.startActionWeekLongDeal(this);
    }

}
