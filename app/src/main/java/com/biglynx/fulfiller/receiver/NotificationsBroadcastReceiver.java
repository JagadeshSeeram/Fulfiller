package com.biglynx.fulfiller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by snehitha.chinnapally on 1/25/2017.
 */

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver","Received");
    }
}
