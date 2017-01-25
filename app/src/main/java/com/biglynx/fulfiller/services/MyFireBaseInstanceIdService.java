package com.biglynx.fulfiller.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by snehitha.chinnapally on 1/25/2017.
 */

public class MyFireBaseInstanceIdService extends FirebaseInstanceIdService {
    private String TAG = "FireBaseInstanceID";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "FireBase Token :: "+tokenRefresh);
        if (tokenRefresh != null)
            sendTokenToServer();
    }

    private void sendTokenToServer() {

    }
}
