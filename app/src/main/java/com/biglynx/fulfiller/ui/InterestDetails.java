package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.models.FulfillerInterests;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.network.HttpAdapter;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AlertDialogManager;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.AppUtil.context;
import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/*
 *
 * Created by Biglynx on 7/21/2016.
 *
*/

public class InterestDetails extends AppCompatActivity implements NetworkOperationListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    ImageView icon_back, price_imv;
    TextView locationtype_tv, day_tv, time_tv, order_tv, total_weight_tv,
            mindistance_tv, maxdistance_tv, point_miles_tv;
    TextView companyname_tv, pickup_loc_tv, pricetype_tv, date_tv, orders_tv, fulfillment_id_tv, fixedprice_tv,
            subtotal, servicefee_tv, totalpay_tv, bid_text_tv, amount_tv, titlebar, status_tv, expiremsg_tv;
    TextView interest_conf_tv, interest_conf_val_tv, interest_exp_tv, interest_exp_val_tv, express_day_tv, express_time_tv, expiration_tv,
            deliverytoken_tv, fulfillmentid_tv, share_cancel_tv, share_copy_tv, share_email_tv, share_sms_tv;
    EditText enter_bid_ev;
    int positon;
    String retaileLocId, completed;
    int positions;
    SimpleDateFormat simpleDateFormat;
    Timer timer, expiratoin_time, exp_time;
    InterestDTO interest;
    LinearLayout expressinterest_LI, cancel_intrest_LI, startdelivery_LI, expressed_LI, share_intrest_LI, share_LI;
    private int year;
    private int month;
    private int day;

    static final int DATE_PICKER_ID = 1111;
    private FullFillerApiWrapper apiWrapper;
    private RelativeLayout cus_address_LI;
    private int READ_PHONE_STATE_PERMISSION = 1;
    private ImageView toolbar_end_imv;
    private TextView due_date_tv;
    private LinearLayout delivery_due_LI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interestexpress);
        timer = new Timer();
        expiratoin_time = new Timer();
        exp_time = new Timer();
        //scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        price_imv = (ImageView) findViewById(R.id.price_imv);

        deliverytoken_tv = (TextView) findViewById(R.id.deliverytoken_tv);
        fulfillmentid_tv = (TextView) findViewById(R.id.fulfillmentid_tv);
        interest_conf_tv = (TextView) findViewById(R.id.interest_conf_tv);
        interest_conf_val_tv = (TextView) findViewById(R.id.interest_conf_val_tv);
        interest_exp_tv = (TextView) findViewById(R.id.interest_exp_tv);
        interest_exp_val_tv = (TextView) findViewById(R.id.interest_exp_val_tv);
        express_day_tv = (TextView) findViewById(R.id.express_day_tv);
        express_time_tv = (TextView) findViewById(R.id.express_time_tv);
        status_tv = (TextView) findViewById(R.id.statustv);
        due_date_tv = (TextView) findViewById(R.id.due_date_tv);
        expiration_tv = (TextView) findViewById(R.id.expiration_tv);
        expiremsg_tv = (TextView) findViewById(R.id.expiremsg_tv);
        delivery_due_LI = (LinearLayout) findViewById(R.id.delivery_due_LI);
        expressed_LI = (LinearLayout) findViewById(R.id.expressed_LI);
        expressinterest_LI = (LinearLayout) findViewById(R.id.expressinterest_LI);
        cancel_intrest_LI = (LinearLayout) findViewById(R.id.cancel_intrest_LI);
        startdelivery_LI = (LinearLayout) findViewById(R.id.startdelivery_LI);

        share_intrest_LI = (LinearLayout) findViewById(R.id.share_intrest_LI);

        share_LI = (LinearLayout) findViewById(R.id.share_LI);
        share_cancel_tv = (TextView) findViewById(R.id.share_cancel_tv);
        share_copy_tv = (TextView) findViewById(R.id.share_copy_tv);
        share_email_tv = (TextView) findViewById(R.id.share_email_tv);
        share_sms_tv = (TextView) findViewById(R.id.share_sms_tv);
        cus_address_LI = (RelativeLayout) findViewById(R.id.cus_address_LI);
        share_LI.setVisibility(View.GONE);

        startdelivery_LI.setOnClickListener(this);
        cancel_intrest_LI.setOnClickListener(this);
        expressinterest_LI.setOnClickListener(this);
        share_intrest_LI.setOnClickListener(this);
        cus_address_LI.setOnClickListener(this);

        share_LI.setOnClickListener(this);
        share_cancel_tv.setOnClickListener(this);
        share_copy_tv.setOnClickListener(this);
        share_email_tv.setOnClickListener(this);
        share_sms_tv.setOnClickListener(this);

        pickup_loc_tv = (TextView) findViewById(R.id.pickup_loc_tv);
        companyname_tv = (TextView) findViewById(R.id.companynames_tv);
        titlebar = (TextView) findViewById(R.id.companyname_tv);

        pricetype_tv = (TextView) findViewById(R.id.pricetype_tv);
        day_tv = (TextView) findViewById(R.id.day_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);
        order_tv = (TextView) findViewById(R.id.order_tv);
        orders_tv = (TextView) findViewById(R.id.orders_tv);
        total_weight_tv = (TextView) findViewById(R.id.total_weight_tv);
        mindistance_tv = (TextView) findViewById(R.id.mindistance_tv);
        maxdistance_tv = (TextView) findViewById(R.id.maxdistance_tv);
        point_miles_tv = (TextView) findViewById(R.id.point_miles_tv);

        // companyname_tv = (TextView) findViewById(R.id.company_tv);
        pickup_loc_tv = (TextView) findViewById(R.id.pickup_loc_tv);
        // pricetype_tv = (TextView) findViewById(R.id.pricetype_tv);
        date_tv = (TextView) findViewById(R.id.date_tv);
        orders_tv = (TextView) findViewById(R.id.orders_tv);
        fulfillment_id_tv = (TextView) findViewById(R.id.fulfillment_id_tv);
        fixedprice_tv = (TextView) findViewById(R.id.fixedprice_tv);
        subtotal = (TextView) findViewById(R.id.subtotal_tv);
        servicefee_tv = (TextView) findViewById(R.id.servicefee_tv);
        totalpay_tv = (TextView) findViewById(R.id.totalpay_tv);
        bid_text_tv = (TextView) findViewById(R.id.bid_text_tv);
        amount_tv = (TextView) findViewById(R.id.amount_tv);

        retaileLocId = getIntent().getStringExtra("interestId");
        completed = getIntent().getStringExtra("completed");

        apiWrapper = new FullFillerApiWrapper();
        if (Common.isNetworkAvailable(MyApplication.getInstance())) {
            Common.showDialog(this);
            //InterestDetails API
            callService();

        } else {
            AppUtil.toast(InterestDetails.this, "Network disconnected. Please check");
        }
    }

    public void callService() {
        apiWrapper.interestInfoCall(AppPreferences.getInstance(InterestDetails.this).getSignInResult().optString("AuthNToken"),
                retaileLocId, new Callback<InterestDTO>() {
                    @Override
                    public void onResponse(Call<InterestDTO> call, Response<InterestDTO> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            InterestDTO interestDTO = response.body();
                            interest = interestDTO;
                            buildUI();
                        } else {
                            try {
                                AppUtil.parseErrorMessage(InterestDetails.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(InterestDetails.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<InterestDTO> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(InterestDetails.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }


    private void buildUI() {

        status_tv.setText("Status: " + interest.Fulfillments.FulfillerInterests.InterestStatus);
        status_tv.setVisibility(View.VISIBLE);
        titlebar.setText(interest.BusinessLegalName.toUpperCase());

        if (interest.Fulfillments.FulfillerInterests.InterestStatus.equals("Expressed")) {
            cancel_intrest_LI.setVisibility(View.VISIBLE);
            expressinterest_LI.setVisibility(View.VISIBLE);
            startdelivery_LI.setVisibility(View.GONE);
            share_intrest_LI.setVisibility(View.GONE);
        } else if (interest.Fulfillments.FulfillerInterests.InterestStatus.equalsIgnoreCase("Confirmed")) {
            cancel_intrest_LI.setVisibility(View.GONE);
            expressinterest_LI.setVisibility(View.GONE);
            startdelivery_LI.setVisibility(View.VISIBLE);
            if (AppPreferences.getInstance(InterestDetails.this).
                    getSignInResult().optString("Role").toLowerCase().contains("partner"))
                share_intrest_LI.setVisibility(View.VISIBLE);
            else
                share_intrest_LI.setVisibility(View.GONE);

        } else {
            cancel_intrest_LI.setVisibility(View.GONE);
            expressinterest_LI.setVisibility(View.GONE);
            startdelivery_LI.setVisibility(View.GONE);
            share_intrest_LI.setVisibility(View.GONE);
        }

        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        toolbar_end_imv = (ImageView) findViewById(R.id.toolbar_end_imv);
        toolbar_end_imv.setVisibility(View.VISIBLE);
        toolbar_end_imv.setImageResource(R.drawable.help_icon_n);
        toolbar_end_imv.setOnClickListener(this);
        companyname_tv.setText(interest.BusinessLegalName.toUpperCase());
        deliverytoken_tv.setText(interest.Fulfillments.FulfillerInterests.ConfirmationCode);
        fulfillmentid_tv.setText(interest.Fulfillments.FulfillmentId);

        /*  pickup_loc_tv.setText(interest.RetailerLocationAddress.RetailerLocationAddress.AddressLine1 + "," +
                interest.RetailerLocationAddress.RetailerLocationAddress.AddressLine2);*/

        fillfromData(positon);

        pickup_loc_tv.setText(interest.RetailerLocationAddress.RetailerLocationAddress.AddressLine1 + ", " +
                interest.RetailerLocationAddress.RetailerLocationAddress.City + ", "
                + interest.RetailerLocationAddress.RetailerLocationAddress.State + ", " +
                interest.RetailerLocationAddress.RetailerLocationAddress.CountryName);

        // pricetype_tv.setText(interest.Fulfillments.PriceType);
        Date date = new Date();
        //date_tv.setText("" + date.toString().replace("GMT+05:30 2016", ""));
        date_tv.setText("" + AppUtil.getLocalDateFormat(interest.Fulfillments.FulfillerInterests.InterestDateTime));
        //fulId=interest.Fulfillments.FulfillmentId;
        fulfillment_id_tv.setText("BROADCAST ID: " + interest.Fulfillments.FulfillmentId);
        amount_tv.setText("$ " + AppUtil.getTwoDecimals(interest.Fulfillments.FulfillerInterests.Amount));
        //orders=""+interest.Fulfillments.Orders.size();

        orders_tv.setText("ORDERS : " + interest.Fulfillments.Orders.size());

        if (interest.Fulfillments.PriceType.toLowerCase().contains("fixed")) {
            double amount = interest.Fulfillments.FulfillerInterests.Amount;
            fixedprice_tv.setText("Fixed Price " + AppUtil.getTwoDecimals(amount) + " $ ");
            subtotal.setText("$ " + AppUtil.getTwoDecimals(amount));
            double value = (amount * (10 / 100.0f));
            servicefee_tv.setText("$ " + AppUtil.getTwoDecimals(value));
            totalpay_tv.setText("$ " + AppUtil.getTwoDecimals((amount - value)));
            // finalAmount="$ " + ((Integer.parseInt(interest.Fulfillments.Amount)) - value);

        } else {
            double amount = interest.Fulfillments.FulfillerInterests.Amount;
            double subtotals = amount + 0;
            fixedprice_tv.setText("Bidding price " + AppUtil.getTwoDecimals(subtotals) + " $");
            subtotal.setText("$ " + AppUtil.getTwoDecimals(subtotals));
            double value = subtotals * (10 / 100.0f);
            servicefee_tv.setText("" + AppUtil.getTwoDecimals(value));
            totalpay_tv.setText("$ " + AppUtil.getTwoDecimals((subtotals - value)));
            // finalAmount="$ " + (subtotals- value);

        }
    }

    private void fillfromData(final int positon) {
        // Timer to display delivery due time
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO do your thing
                        try {
                            Date interestDueDate = simpleDateFormat.parse(interest.Fulfillments.ExpirationDateTime);
                            Date todayDate = new Date();
                            System.out.println("service Date  " + interestDueDate + ",current date" + todayDate);
                            printDifference(todayDate, interestDueDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 1000);

        try {

            //Date myDates = simpleDateFormat.parse(interest.Fulfillments.FulfillerInterests.InterestDateTime);
            //Interest created time
            interest_exp_val_tv.setText("" + AppUtil.getLocalDateFormat(interest.Fulfillments.FulfillerInterests.InterestDateTime));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Timer to display interest expiration time,if interest is not expired
        exp_time.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO do your thing
                        try {
                            Date interestExpDate = simpleDateFormat.parse(interest.Fulfillments.FulfillerInterests.InterestExpirationDateTime);
                            Date todayLocalDate = new Date();
                            displayExpire(todayLocalDate, interestExpDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 1000);

        order_tv.setText("" + interest.Fulfillments.Orders.size());
        total_weight_tv.setText(interest.Fulfillments.TotalWeight + " Lbs");
        mindistance_tv.setText(interest.Fulfillments.TotalDistance + " Miles");
        maxdistance_tv.setText(TimeUnit.SECONDS.toMinutes(Long.parseLong(interest.Fulfillments.TotalApproxTimeInSeconds)) + " Min");
        due_date_tv.setText("" + AppUtil.getLocalDateFormat(interest.Fulfillments.ExpirationDateTime));
        // point_miles_tv.setText(interest.Fulfillments.TotalApproxTimeInSeconds + " Miles");


        if (interest.Fulfillments.PriceType.toLowerCase().contains("fixed")) {
            pricetype_tv.setText(AppUtil.getTwoDecimals(interest.Fulfillments.Amount) + "$ FIXED PRICE");
            price_imv.setImageResource(R.drawable.fixed_price);
            pricetype_tv.setTextColor(Color.parseColor("#89A469"));

        } else {
            pricetype_tv.setText("BIDDING PRICE");
            pricetype_tv.setTextColor(Color.parseColor("#ED7165"));
            price_imv.setImageResource(R.drawable.bidding_price);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        expiratoin_time.cancel();
        exp_time.cancel();
    }


    @Override
    public void operationCompleted(NetworkResponse networkResponse) {

        if (networkResponse.getStatusCode() == 200) {

            try {
                JSONObject jsonObject = new JSONObject(networkResponse.getResponseString());
                interest = new Gson().fromJson(jsonObject.toString(), InterestDTO.class);

                buildUI();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

        }

        Common.disMissDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        positions = position;
        fillfromData(position);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.icon_back:
                finish();
                break;
            case R.id.toolbar_end_imv:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.eyece.com/Index.html#/help"));
                startActivity(browserIntent);
                break;
            case R.id.expressinterest_LI:
                showDialog(DATE_PICKER_ID);
                break;
            case R.id.startdelivery_LI:
                getpermissions();
                break;

            case R.id.cancel_intrest_LI:
                cancelInterest();
                break;
            /*case R.id.share_LI:
                share_LI.setVisibility(View.GONE);
                break;*/
            case R.id.share_sms_tv:
                share_LI.setVisibility(View.GONE);
                sendSMSMessage();
                break;
            case R.id.share_email_tv:
                sendEmail();
                break;
            case R.id.share_copy_tv:
                try {
                   /* android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                            .getSystemService(context.CLIPBOARD_SERVICE);
                    clipboard.setText("Confirmation code: " + interest.Fulfillments.FulfillerInterests.ConfirmationCode + "\nFulfillment Id : " +
                            interest.Fulfillments.FulfillmentId);*/
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            getSystemService(context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData
                            .newPlainText("Details", "Confirmation code: " + interest.Fulfillments.FulfillerInterests.ConfirmationCode + "\nFulfillment Id : " +
                                    interest.Fulfillments.FulfillmentId +
                                    "\nFulFiller Id : " + AppPreferences.getInstance(InterestDetails.this).getSignInResult().optString("FulfillerId"));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(InterestDetails.this, "Copied to Clipboard!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                share_LI.setVisibility(View.GONE);
                break;
            case R.id.share_cancel_tv:
                share_LI.setVisibility(View.GONE);
                break;

            case R.id.cus_address_LI:
                startActivity(new Intent(this, CustomerAddress.class)
                        .putExtra("interest", interest)
                        .putExtra("position", positions));
                break;
        }
    }

    private void cancelInterest() {
        if (!Common.isNetworkAvailable(MyApplication.getInstance())) {
            AppUtil.toast(InterestDetails.this, "Network disconnected. Please check");
            return;
        }
        Common.showDialog(InterestDetails.this);
        apiWrapper.cancelInterest(AppPreferences.getInstance(getApplicationContext()).getSignInResult() != null ?
                        AppPreferences.getInstance(getApplicationContext()).getSignInResult().optString("AuthNToken") : "",
                interest.Fulfillments.FulfillerInterestId, new Callback<FulfillerInterests>() {
                    @Override
                    public void onResponse(Call<FulfillerInterests> call, Response<FulfillerInterests> response) {
                        if (response.isSuccessful()) {
                            AppUtil.toast(InterestDetails.this, "Interest is cancelled...");
                            finish();
                        } else {
                            try {
                                AppUtil.parseErrorMessage(InterestDetails.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(InterestDetails.this, "Unable to cancel the interest. Please try later...");
                                e.printStackTrace();
                            }
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<FulfillerInterests> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(InterestDetails.this, "Unable to cancel the interest. Please try later...");
                    }
                });
    }

    private void getpermissions() {
        int sdkVersion = Build.VERSION.SDK_INT;

        if (sdkVersion >= 23) {
            // Getting permissins to Read from External Storage
            getPermissionsToReadPhoneState();
        } else
            callStartDeliveryService();
    }

    private Object getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = telephonyManager.getDeviceId();
        Log.d(StartDelivery.class.getSimpleName(), "Device ID :: " + deviceID);
        return deviceID;
    }

    public void getPermissionsToReadPhoneState() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.permisn_denied));
                builder.setMessage(getString(R.string.explantn_for_requstng_permissn));
                builder.setPositiveButton("RE-TRY", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User has asked the permission again.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(InterestDetails.this, new
                                                    String[]{android.Manifest.permission.READ_PHONE_STATE},
                                            READ_PHONE_STATE_PERMISSION);
                                }
                            }
                        });
                builder.setNegativeButton("IM SURE", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User has denied the permission.
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                return;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(InterestDetails.this, new
                                    String[]{android.Manifest.permission.READ_PHONE_STATE},
                            READ_PHONE_STATE_PERMISSION);
                    return;
                }
            }
        } else {
            //Permission already granted
            callStartDeliveryService();
        }

    }

    // Callback with the request from calling requestPermissions(...)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_PHONE_STATE_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                callStartDeliveryService();
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog dialog = new AlertDialog.Builder(InterestDetails.this)
                            .setMessage(getString(R.string.accept_permission_in_settings))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }
        }
    }


    private void callStartDeliveryService() {
        if (!Common.isNetworkAvailable(MyApplication.getInstance())) {
            AppUtil.toast(InterestDetails.this, "Network disconnected. Please check");
            return;
        }

        Common.showDialog(InterestDetails.this);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fulfillerid", AppPreferences.getInstance(InterestDetails.this).getSignInResult().optString("FulfillerId"));
        hashMap.put("confirmationcode", interest.Fulfillments.FulfillerInterests.ConfirmationCode);
        hashMap.put("name", interest.Fulfillments.LocationContactPerson);
        hashMap.put("latitude", interest.Fulfillments.PickUpMapLatitude);//47.6062`
        hashMap.put("longitude", interest.Fulfillments.PickUpMapLongitude);//122.3321
        hashMap.put("deviceid", getDeviceId());

        apiWrapper.startDeliveryCall(AppPreferences.getInstance(InterestDetails.this).getSignInResult() != null ?
                        AppPreferences.getInstance(InterestDetails.this).getSignInResult().optString("AuthNToken") : "",
                hashMap, new Callback<InterestDTO>() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(Call<InterestDTO> call, Response<InterestDTO> response) {
                        if (response.isSuccessful()) {
                            InterestDTO responseInterestObj = response.body();
                            if (responseInterestObj != null) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("responseInterest", responseInterestObj);
                                bundle.putString("fulfillerId", null);
                                startActivity(new Intent(InterestDetails.this, StartDelivery.class)
                                        .putExtras(bundle));
                            } else {
                                AppUtil.toast(InterestDetails.this, "The response is empty");
                            }
                        } else {
                            try {
                                AppUtil.parseErrorMessage(InterestDetails.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(InterestDetails.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<InterestDTO> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(InterestDetails.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });

    }

    protected void sendSMSMessage() {


/*
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("", null, "Confirmation code: "+interest.Fulfillments.FulfillerInterests.ConfirmationCode+"\nFulfillment Id : "+
                    interest.Fulfillments.FulfillmentId, null, null);

        }

        catch (Exception e) {
            AlertDialogManager.showAlertOnly(this,"Fulfiller","Error sending SMS","Ok");
            e.printStackTrace();
        }*/
        Intent message = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
        message.putExtra("sms_body", "Confirmation code: " + interest.Fulfillments.FulfillerInterests.ConfirmationCode + "\nFulfillment Id : " +
                interest.Fulfillments.FulfillmentId + "\nFulFiller Id : " + AppPreferences.getInstance(InterestDetails.this).getSignInResult().optString("FulfillerId"));
        startActivity(message);
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fulfiller Details");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Confirmation code: " + interest.Fulfillments.FulfillerInterests.ConfirmationCode + "\nFulfillment Id : " +
                interest.Fulfillments.FulfillmentId + "\nFulFiller Id : " + AppPreferences.getInstance(InterestDetails.this).getSignInResult().optString("FulfillerId"));

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            share_LI.setVisibility(View.GONE);
        } catch (android.content.ActivityNotFoundException ex) {
            AlertDialogManager.showAlertOnly(this, "Fulfiller", "Error sending Email", "Ok");
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            Date dates = null;
            year = selectedYear;
            month = Integer.valueOf(checkDigit(selectedMonth));
            day = selectedDay;
            final String date = checkDigit(month + 1) + "/" + checkDigit(day) + "/" + year;
            final SimpleDateFormat simpleDateFormats = new SimpleDateFormat("MM/dd/yyyy");

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(InterestDetails.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, final int hourOfDay,
                                              final int minute) {

                            final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm");

                            interest_exp_val_tv.setText(date.replace("\"", "") + "," + hourOfDay + ":" + minute);
                            JSONObject addvehilce = new JSONObject();

                            try {
                                addvehilce.put("FulfillerInterestId", interest.Fulfillments.FulfillerInterests.FulfillerInterestId);
                                addvehilce.put("InterestExpirationDateTime", date + " " + hourOfDay + ":" + minute/*date.replace("\"","")+ " "+hourOfDay + ":" + minute*/);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            HttpAdapter.extendexpiration(InterestDetails.this, "extendexpiration", addvehilce.toString().replace("\\", ""));
                            expiratoin_time.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO do your thing
                                            Date datess = null;
                                            try {
                                                datess = sdf.parse(checkDigit(month + 1) + "-" + checkDigit(day) + "-" + year + " " + hourOfDay + ":" + minute);
                                                Date curent = new Date();
                                                getDifferenceTime(curent, datess);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }, 0, 1000);
                        }
                    }, mHour, mMinute, false);

            timePickerDialog.show();

        }
    };


    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400

    public void printDifference(Date todayDate, Date interestDueDate) {
        //milliseconds
        long different = interestDueDate.getTime() - todayDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        day_tv.setText("" + elapsedDays + " Days");
        time_tv.setText("" + elapsedHours + "H : " + elapsedMinutes + "M : " + elapsedSeconds + "S");

        if (String.valueOf(elapsedSeconds).startsWith("-") || String.valueOf(elapsedDays).startsWith("-") ||
                String.valueOf(elapsedHours).startsWith("-") || String.valueOf(elapsedMinutes).startsWith("-")) {
            if (completed.equals("not")) {
                if (!expiremsg_tv.isShown())
                    expiremsg_tv.setVisibility(View.VISIBLE);
                if (delivery_due_LI.isShown())
                    delivery_due_LI.setVisibility(View.GONE);
            }
        } else {
            if (expiremsg_tv.isShown())
                expiremsg_tv.setVisibility(View.GONE);
            if (!delivery_due_LI.isShown())
                delivery_due_LI.setVisibility(View.VISIBLE);
        }
    }

    public void displayExpire(Date todayLocalDate, Date interestExpDate) {
        //milliseconds
        //if interestExpDate is greater than todayLocalDate, that means interest has already expired is not expired.
        //Display interest expiation time
        long different = interestExpDate.getTime() - todayLocalDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        /*day_tv.setText("" + elapsedDays + " Days");
        time_tv.setText("" + elapsedHours + "H : " + elapsedMinutes + "M : " + elapsedSeconds + "S");*/

        if (String.valueOf(elapsedSeconds).startsWith("-") || String.valueOf(elapsedDays).startsWith("-") ||
                String.valueOf(elapsedHours).startsWith("-") || String.valueOf(elapsedMinutes).startsWith("-")) {
            if (expiration_tv.isShown())
                expiration_tv.setVisibility(View.GONE);
        } else {
            if (!expiration_tv.isShown())
                expiration_tv.setVisibility(View.VISIBLE);
            if (!interest.Fulfillments.FulfillerInterests.InterestStatus.equalsIgnoreCase("Confirmed") &&
                    !cancel_intrest_LI.isShown())
                cancel_intrest_LI.setVisibility(View.VISIBLE);
            expiration_tv.setText("Interest Expirs IN : " + elapsedDays + "D :" + elapsedHours + "H :" + elapsedMinutes + "M :" + elapsedSeconds + "S");
        }
    }

    public void getDifferenceTime(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        Log.d("diff is", "" + elapsedDays);

         /*
         if(String.valueOf(elapsedSeconds).startsWith("-")){
            expressed_LI.setVisibility(View.GONE);
        }else{
            expressed_LI.setVisibility(View.VISIBLE);

        }*/
        if (String.valueOf(elapsedSeconds).startsWith("-")) {

        } else {

            expiration_tv.setText("Expire Date : " + elapsedDays + "D :" + elapsedHours + "H :" + elapsedMinutes + "M :" + elapsedSeconds + "S");
        }
    }
}
