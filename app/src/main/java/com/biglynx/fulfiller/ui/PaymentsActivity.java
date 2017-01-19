package com.biglynx.fulfiller.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;

public class PaymentsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iconBack;
    private TextView configuration_tv;
    private TextView payments_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        initViews();
        initLoadFirstFragment();
    }

    private void initLoadFirstFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container_payouts,new AccountConfigFragment()).commit();
    }

    private void initViews() {
        iconBack = (ImageView) findViewById(R.id.icon_back);
        configuration_tv = (TextView) findViewById(R.id.configuration_tv);
        payments_tv = (TextView) findViewById(R.id.payments_tv);

        iconBack.setOnClickListener(this);
        configuration_tv.setOnClickListener(this);
        payments_tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.configuration_tv:
                configuration_tv.setBackgroundResource(R.drawable.lef_roundedcorner);
                payments_tv.setBackgroundResource(R.drawable.lef_roundedcorner_trans);
                configuration_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                payments_tv.setTextColor(Color.parseColor("#FFFFFF"));
                initLoadFirstFragment();
                break;
            case R.id.payments_tv:
                configuration_tv.setBackgroundResource(R.drawable.right_roundedcorner_trans);
                payments_tv.setBackgroundResource(R.drawable.right_roundedcorner);
                payments_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                configuration_tv.setTextColor(Color.parseColor("#FFFFFF"));
                getSupportFragmentManager().beginTransaction().add(R.id.container_payouts,new PaymentDetailsFragment()).commit();
                break;
        }
    }
}
