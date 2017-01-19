package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.biglynx.fulfiller.MainActivity;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity {

    class C03871 implements Runnable {
        C03871() {
        }

        public void run() {
            if(AppPreferences.getInstance(SplashActivity.this).getSignInResult() != null){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            else{
                startActivity(new Intent(SplashActivity.this, InitialScreen.class));
            }

          finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splashactivity);

        Handler handler = new Handler();

        handler.postDelayed(new C03871(), 3000);
    }

}
