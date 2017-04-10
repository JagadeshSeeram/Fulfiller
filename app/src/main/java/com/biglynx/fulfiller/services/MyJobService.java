package com.biglynx.fulfiller.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends Service {
    private static final String TAG = "SyncService";
    Handler handler = null;
    private final long INTERVAL = 120 * 1000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (handler == null)
            handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Service Started :: "+System.currentTimeMillis());
                handler.postDelayed(this,INTERVAL);
            }
        };
        handler.postDelayed(runnable,0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
