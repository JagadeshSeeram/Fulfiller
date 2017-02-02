package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillerInterests;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AlertDialogManager;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/**
 * Created by Biglynx on 8/8/2016.
 */

public class Invoice extends AppCompatActivity implements NetworkOperationListener,
        View.OnClickListener {

    ImageView icon_back, companylogo_imv;
    TextView companyname_tv, pickup_loc_tv, pricetype_tv, date_tv, orders_tv, fulfillment_id_tv, fixedprice_tv,
            subtotal, servicefee_tv, totalpay_tv, bid_text_tv, amount_tv, titlebar;
    CheckBox bid_licence;
    Button submit_btn;
    EditText bidtime_ev;
    BroadCast broadCast;
    int positon;
    String bidamount;
    double finalAmount;
    String orders;
    String fulId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private FullFillerApiWrapper apiWrapper;
    private double subtotals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice);

        broadCast = getIntent().getParcelableExtra("broadCast");
        positon = getIntent().getIntExtra("position", 1);
        bidamount = getIntent().getStringExtra("bidamount");
        titlebar = (TextView) findViewById(R.id.companyname_tv);
        titlebar.setText("BROADCAST INTEREST");
        icon_back = (ImageView) findViewById(R.id.icon_back);
        companylogo_imv = (ImageView) findViewById(R.id.companylogo_imv);
        companyname_tv = (TextView) findViewById(R.id.company_tv);
        pickup_loc_tv = (TextView) findViewById(R.id.pickup_loc_tv);
        pricetype_tv = (TextView) findViewById(R.id.pricetype_tv);
        date_tv = (TextView) findViewById(R.id.date_tv);
        orders_tv = (TextView) findViewById(R.id.orders_tv);
        fulfillment_id_tv = (TextView) findViewById(R.id.fulfillment_id_tv);
        fixedprice_tv = (TextView) findViewById(R.id.fixedprice_tv);
        subtotal = (TextView) findViewById(R.id.subtotal_tv);
        servicefee_tv = (TextView) findViewById(R.id.servicefee_tv);
        totalpay_tv = (TextView) findViewById(R.id.totalpay_tv);
        bid_text_tv = (TextView) findViewById(R.id.bid_text_tv);
        amount_tv = (TextView) findViewById(R.id.amount_tv);

        bid_licence = (CheckBox) findViewById(R.id.bid_licence);
        bidtime_ev = (EditText) findViewById(R.id.bidtime_ev);
        submit_btn = (Button) findViewById(R.id.bid_now_btn);
        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        companyname_tv.setText(broadCast.BusinessLegalName);
        pickup_loc_tv.setText(broadCast.RetailerLocationAddress.RetailerLocationAddress.AddressLine1 + "," +
                broadCast.RetailerLocationAddress.RetailerLocationAddress.City + "," + broadCast.RetailerLocationAddress.RetailerLocationAddress.CountryName);
        // pricetype_tv.setText(broadCast.Fulfillments.get(positon).PriceType);
        Date date = new Date();
        date_tv.setText("" + date);
        fulId = broadCast.Fulfillments.get(positon).FulfillmentId;
        fulfillment_id_tv.setText("BroadCast ID " + fulId);

        if (broadCast.Fulfillments.get(positon).PriceType.toLowerCase().contains("bidding"))
            amount_tv.setText("$ "+AppUtil.getTwoDecimals(Double.parseDouble(bidamount)));
        else
            amount_tv.setText("$ " + AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).Amount));

        orders = "" + broadCast.Fulfillments.get(positon).Orders.size();
        orders_tv.setText("ORDERS : " + orders);

        if (broadCast.Fulfillments.get(positon).PriceType.toLowerCase().contains("fixed")) {
            fixedprice_tv.setText("Fixed Price " + AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).Amount) + " $ ");
            subtotal.setText("$ " + AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).Amount));
            float value = Float.parseFloat(String.valueOf((broadCast.Fulfillments.get(positon).Amount) * (10 / 100.0f)));
            servicefee_tv.setText("" + value);
            totalpay_tv.setText("$ " + AppUtil.getTwoDecimals((broadCast.Fulfillments.get(positon).Amount) - value));
            finalAmount = (broadCast.Fulfillments.get(positon).Amount) - value;

        } else {

            double amount = broadCast.Fulfillments.get(positon).Amount;
            subtotals = amount + Double.parseDouble(bidamount);
            fixedprice_tv.setText("Bidding Price $" + AppUtil.getTwoDecimals(Double.parseDouble(bidamount)));
            subtotal.setText("$ " + AppUtil.getTwoDecimals(subtotals));
            double value =  subtotals * (10 / 100.0f);
            servicefee_tv.setText("" + AppUtil.getTwoDecimals(value));
            totalpay_tv.setText("$ " +  AppUtil.getTwoDecimals(subtotals - value));
            finalAmount = subtotals - value;
        }


/*
        if(Common.isNetworkAvailable(this)){
            HttpAdapter.broadCast(this,"fulfillments");
        }*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void operationCompleted(NetworkResponse networkResponse) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.bid_now_btn:
                if (AppPreferences.getInstance(Invoice.this).getSignInResult() != null) {
                    Log.e(Invoice.class.getSimpleName(), "FulFillerID :: " + AppPreferences.getInstance(Invoice.this).getSignInResult().optString("FulfillerId"));
                    Log.e(Invoice.class.getSimpleName(), "FulFilmentId :: " + broadCast.Fulfillments.get(positon).FulfillmentId);
                    Log.e(Invoice.class.getSimpleName(), "Price Type :: " + broadCast.Fulfillments.get(positon).PriceType);
                    Log.e(Invoice.class.getSimpleName(), "Amount :: " + broadCast.Fulfillments.get(positon).Amount);
                }

                if (!Common.isNetworkAvailable(Invoice.this)) {
                    AppUtil.toast(Invoice.this, "Network Disconnected. Please check...");
                    return;
                }

                Common.showDialog(Invoice.this);
                apiWrapper = new FullFillerApiWrapper();

                HashMap hashMap = new HashMap<>();
                hashMap.put("FulfillmentId", broadCast != null ? broadCast.Fulfillments.get(positon).FulfillmentId : "");
                hashMap.put("FulfillerId", AppPreferences.getInstance(Invoice.this).getSignInResult() != null ? AppPreferences.getInstance(Invoice.this).getSignInResult().optString("FulfillerId") : "");
                hashMap.put("PriceType", broadCast != null ? broadCast.Fulfillments.get(positon).PriceType.replaceAll(" ", "%20") : "");
                if (broadCast.Fulfillments.get(positon).PriceType.toLowerCase().contains("bidding"))
                    hashMap.put("Amount",subtotals);
                else
                    hashMap.put("Amount", broadCast != null ? AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).Amount) : "");
                apiWrapper.fulfillerInterestsCall(AppPreferences.getInstance(Invoice.this).getSignInResult().optString("AuthNToken"),
                        hashMap, new Callback<FulfillerInterests>() {
                            @Override
                            public void onResponse(Call<FulfillerInterests> call, Response<FulfillerInterests> response) {
                                Common.disMissDialog();
                                if (response.isSuccessful()) {

                                    FulfillerInterests fulfillerInterests = response.body();
                                    if (fulfillerInterests != null)
                                        Log.d(Invoice.class.getSimpleName(), "fulfillerInterests Successful :: " + "true");
                                    else
                                        Log.d(Invoice.class.getSimpleName(), "fulfillerInterests Successful :: " + "false");

                                    if (bidtime_ev.getText().toString().equals("")) {
                                        AlertDialogManager.showAlertOnly(Invoice.this, "Fulfiller", "Please enter Bid expiration time", "Ok");
                                    } else {
                                        if (broadCast.Fulfillments.get(positon).PriceType.toLowerCase().contains("fixed")) {
                                            startActivity(new Intent(Invoice.this, ThankYou.class)
                                                    .putExtra("finalamount", finalAmount)
                                                    .putExtra("price", "Fixed Price")
                                                    .putExtra("fulID", fulId)
                                                    .putExtra("orders", orders));
                                        } else {
                                            startActivity(new Intent(Invoice.this, ThankYou.class)
                                                    .putExtra("finalamount", finalAmount)
                                                    .putExtra("price", "BID Price")
                                                    .putExtra("fulID", fulId)
                                                    .putExtra("orders", orders));

                                        }
                                    }
                                } else {
                                    try {
                                        AppUtil.parseErrorMessage(Invoice.this, response.errorBody().string());
                                    } catch (IOException e) {
                                        AppUtil.toast(Invoice.this, OOPS_SOMETHING_WENT_WRONG);
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<FulfillerInterests> call, Throwable t) {
                                Common.disMissDialog();
                                AppUtil.toast(Invoice.this, OOPS_SOMETHING_WENT_WRONG);
                            }
                        });
                break;

        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Invoice Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
