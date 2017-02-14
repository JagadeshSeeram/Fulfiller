package com.biglynx.fulfiller.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;

public class PaymentsDetailInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon_back;
    private TextView companyname_tv;
    private TextView fulfillmentid_tv,retailerName_tv,interestDate_tv,
    ordersDelivered_tv,totalWeight_tv,totalPayout_tv,paymentStatus_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_detail_info);

        initViews();
    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        fulfillmentid_tv = (TextView) findViewById(R.id.fulfillmentid_tv);
        retailerName_tv = (TextView) findViewById(R.id.retailer_name_tv);
        interestDate_tv = (TextView) findViewById(R.id.interest_date__tv);
        ordersDelivered_tv = (TextView) findViewById(R.id.orders_delivered_tv);
        totalWeight_tv = (TextView) findViewById(R.id.total_weight_tv);
        totalPayout_tv = (TextView) findViewById(R.id.total_payout_tv);
        paymentStatus_tv = (TextView) findViewById(R.id.payment_status_tv);

        icon_back = (ImageView) findViewById(R.id.icon_back);
        companyname_tv.setVisibility(View.VISIBLE);
        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        companyname_tv.setText("payment details");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
        }
    }
}
