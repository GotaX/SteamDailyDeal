package com.gota.steamdailydeal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class WorkService extends IntentService {

    private static final String ACTION_UPDATE_DATA = "com.gota.steamdailydeal.action.UPDATE_DATA";

    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, WorkService.class);
        intent.setAction(ACTION_UPDATE_DATA);
        context.startService(intent);
    }

    public WorkService() {
        super("WorkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_DATA:
                    handleActionUpdateData();
                    break;
            }
        }
    }

    private void handleActionUpdateData() {

    }

    private void getDailyDeal() {

    }
}
