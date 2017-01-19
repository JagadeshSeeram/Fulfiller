package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.MainActivity;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AppUtil;

/**
 * Created by Biglynx on 8/8/2016.
 */

public class ThankYou extends AppCompatActivity implements NetworkOperationListener,
        View.OnClickListener {

    LinearLayout icon_back;
    TextView amount_tv,intrest_id_tv,final_amount_tv,fixedprice_tv,fulfillment_id_tv,delivery_count_tv,
            totalpay_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thankyou);

        double finalamount=getIntent().getDoubleExtra("finalamount",0.00);
        String  price=getIntent().getStringExtra("price");
        String  fulID=getIntent().getStringExtra("fulID");
        String  orders=getIntent().getStringExtra("orders");


        icon_back=(LinearLayout)findViewById(R.id.LL_home_back);
        amount_tv=(TextView) findViewById(R.id.amount_tv);
        intrest_id_tv=(TextView) findViewById(R.id.intrest_id_tv);
        final_amount_tv=(TextView) findViewById(R.id.final_amount_tv);
        fixedprice_tv=(TextView) findViewById(R.id.fixedprice_tv);
        fulfillment_id_tv=(TextView) findViewById(R.id.fulfillment_id_tv);
        delivery_count_tv=(TextView) findViewById(R.id.delivery_count_tv);
        totalpay_tv=(TextView) findViewById(R.id.totalpay_tv);

        amount_tv.setText(AppUtil.getTwoDecimals(finalamount));
        final_amount_tv.setText(AppUtil.getTwoDecimals(finalamount));
        totalpay_tv.setText(AppUtil.getTwoDecimals(finalamount));
        fixedprice_tv.setText(price);
        fulfillment_id_tv.setText("Broadcast Id "+fulID);
        delivery_count_tv.setText(orders+" Deliveries");
        icon_back.setOnClickListener(this);

    }

    @Override
    public void operationCompleted(NetworkResponse networkResponse) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LL_home_back:
                finishActivity();
                break;

        }
    }
    public void finishActivity() {
        Intent intent = new Intent(ThankYou.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}