package com.biglynx.fulfiller.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.PaymentDetailsModel;
import com.biglynx.fulfiller.utils.AppUtil;

public class PaymentsDetailInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon_back;
    private TextView companyname_tv;
    private TextView fulfillmentid_tv, retailerName_tv, interestDate_tv,
            ordersDelivered_tv, totalWeight_tv, totalPayout_tv, paymentStatus_tv;
private PaymentDetailsModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_detail_info);

        initViews();

        if (getIntent().getExtras() != null) {
            model = getIntent().getExtras().getParcelable(PaymentDetailsFragment.PAYOUT_DETAILS);
            if (model != null)
                buildUI();
        }
    }

    private void buildUI() {
        //build Ui
        fulfillmentid_tv.setText(model.FulfillmentId);
        retailerName_tv.setText(model.RetailerName);
        interestDate_tv.setText(AppUtil.getLocalDateFormat(model.DateCreated));
        ordersDelivered_tv.setText(model.ordersCount);
        totalWeight_tv.setText(model.totalWeight + " Kgs");
        totalPayout_tv.setText("$ "+AppUtil.getTwoDecimals(model.PayoutAmount));
        paymentStatus_tv.setText(model.PayoutStatus);
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
