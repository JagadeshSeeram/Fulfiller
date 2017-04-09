package com.biglynx.fulfiller.services;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "on start job: " + System.currentTimeMillis()+" ID :: "+ jobParameters.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
