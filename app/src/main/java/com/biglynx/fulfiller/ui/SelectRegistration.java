package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.biglynx.fulfiller.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Biglynx on 7/20/2016.
 *
 */

public class SelectRegistration extends FragmentActivity implements View.OnClickListener{

LinearLayout busniess_LI,individual_LI;
    ImageView icon_back;
    private ImageView loginBgImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_register);
        busniess_LI=(LinearLayout)findViewById(R.id.business_LI);
        individual_LI=(LinearLayout)findViewById(R.id.individual_LI);
        icon_back=(ImageView)findViewById(R.id.icon_back);
        loginBgImageView = (ImageView) findViewById(R.id.login_bg_image);
        Picasso.with(SelectRegistration.this).load(R.drawable.login_bg).error((int) R.color.colorPrimary).into(loginBgImageView);
        //icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        busniess_LI.setOnClickListener(this);
        individual_LI.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.business_LI:
        startActivity(new Intent(this,BusinessRegistration.class)
        .putExtra("type","0"));
        break;
    case R.id.individual_LI:
        startActivity(new Intent(this,BusinessRegistration.class)
                .putExtra("type","1"));
        break;
    case R.id.icon_back:
      finish();
        break;
}
    }
}
