package com.biglynx.fulfiller.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.biglynx.fulfiller.R;


public class ExplinFulfiller extends AppCompatActivity {
    ImageView imageView;
    protected void onCreate(Bundle savedInstanceState) {
        // To make activity full screen.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fulfillment_image);

    }
}
