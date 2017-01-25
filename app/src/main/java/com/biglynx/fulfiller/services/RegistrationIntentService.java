package com.biglynx.fulfiller.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by snehitha.chinnapally on 1/25/2017.
 */

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    public RegistrationIntentService(){
        super(TAG);

    }
}
