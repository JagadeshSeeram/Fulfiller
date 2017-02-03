package com.biglynx.fulfiller.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.FufillerDetails_Adapter;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.network.HttpAdapter;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.services.GPSTracker;
import com.biglynx.fulfiller.utils.AlertDialogManager;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.biglynx.fulfiller.utils.HorizontalListView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/*
 *
 * Created by Biglynx on 7/21/2016.
 *
*/

public class FulfillmentDetails extends AppCompatActivity implements
         View.OnClickListener, OnMapReadyCallback {

    ImageView icon_back, price_imv;
    TextView companyname_tv, pickup_loc_tv, locationtype_tv, pricetype_tv, day_tv, time_tv, orders_tv, total_weight_tv,
            mindistance_tv, maxdistance_tv, point_miles_tv, bid_text_tv, fulfiment1_tv, fulfiment2_tv, deliverytoken_tv, fulfillmentid_tv;
    CheckBox bid_licence;
    Button bid_now_btn;
    EditText enter_bid_ev;
    BroadCast broadCast;
    LinearLayout fulfillments_count_LI, cus_address_LI;
    int positon;
    String retaileLocId;
    HorizontalListView listivew;
    FufillerDetails_Adapter fufillerDetailsAdapter;
    GoogleMap googleMap;
    List<Marker> markerList;
    GPSTracker mGPS;
    int positions;
    SimpleDateFormat simpleDateFormat;
    Timer timer;
    ImageView current_location_tv;
    ScheduledExecutorService scheduleTaskExecutor;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private FullFillerApiWrapper apiWrapper;
    private TextView locationtype_text_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fulfillment_detailpage);

        MapsInitializer.initialize(getApplicationContext());

        mGPS = new GPSTracker(this);

        //scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        locationtype_text_view = (TextView) findViewById(R.id.locationtype_text_view);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        price_imv = (ImageView) findViewById(R.id.price_imv);
        listivew = (HorizontalListView) findViewById(R.id.listivew);
        markerList = new ArrayList<>();
        pickup_loc_tv = (TextView) findViewById(R.id.pickup_loc_tv);
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        locationtype_tv = (TextView) findViewById(R.id.locationtype_tv);
        pricetype_tv = (TextView) findViewById(R.id.pricetype_tv);
        day_tv = (TextView) findViewById(R.id.day_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);
        orders_tv = (TextView) findViewById(R.id.orders_tv);
        total_weight_tv = (TextView) findViewById(R.id.total_weight_tv);
        mindistance_tv = (TextView) findViewById(R.id.mindistance_tv);
        maxdistance_tv = (TextView) findViewById(R.id.maxdistance_tv);
        point_miles_tv = (TextView) findViewById(R.id.point_miles_tv);
        current_location_tv = (ImageView) findViewById(R.id.current_location_tv);
        fulfiment1_tv = (TextView) findViewById(R.id.fulfiment1_tv);
        fulfiment2_tv = (TextView) findViewById(R.id.fulfiment2_tv);
        fulfillments_count_LI = (LinearLayout) findViewById(R.id.fulfillments_count_LI);

        deliverytoken_tv = (TextView) findViewById(R.id.deliverytoken_tv);
        fulfillmentid_tv = (TextView) findViewById(R.id.fulfillmentid_tv);

        cus_address_LI = (LinearLayout) findViewById(R.id.cus_address_LI);
        cus_address_LI.setOnClickListener(this);
        current_location_tv.setOnClickListener(this);
        bid_text_tv = (TextView) findViewById(R.id.bid_text_tv);
        bid_licence = (CheckBox) findViewById(R.id.bid_licence);
        enter_bid_ev = (EditText) findViewById(R.id.enter_bid_ev);
        bid_now_btn = (Button) findViewById(R.id.bid_now_btn);

        if (getIntent().hasExtra("retaileLocId")) {
            positon = getIntent().getIntExtra("position", 1);
            retaileLocId = getIntent().getStringExtra("retaileLocId");
        }

        if (!TextUtils.isEmpty(retaileLocId))
            Log.e(FulfillmentDetails.class.getSimpleName(), "retaileLocId :: " + retaileLocId);


        positions = positon;
        apiWrapper = new FullFillerApiWrapper();
        if (Common.isNetworkAvailable(MyApplication.getInstance())) {
            Common.showDialog(this);
            apiWrapper.broadCastDetailsCall(AppPreferences.getInstance(FulfillmentDetails.this).getSignInResult() != null ?
                    AppPreferences.getInstance(FulfillmentDetails.this).getSignInResult().optString("AuthNToken") : "",
                    retaileLocId, new Callback<ArrayList<BroadCast>>() {
                        @Override
                        public void onResponse(Call<ArrayList<BroadCast>> call, Response<ArrayList<BroadCast>> response) {
                            Common.disMissDialog();
                            if (response.isSuccessful()) {
                                bid_now_btn.setClickable(true);
                                List<BroadCast> resultBroadCast = response.body();
                                if (resultBroadCast != null && resultBroadCast.size() > 0)
                                    broadCast = resultBroadCast.get(0);
                                buildUI();

                            } else {
                                bid_now_btn.setClickable(false);
                                try {
                                    AppUtil.parseErrorMessage(FulfillmentDetails.this, response.errorBody().string());
                                } catch (IOException e) {
                                    AppUtil.toast(FulfillmentDetails.this, OOPS_SOMETHING_WENT_WRONG);
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<BroadCast>> call, Throwable t) {
                            bid_now_btn.setClickable(false);
                            Common.disMissDialog();
                            AppUtil.toast(FulfillmentDetails.this, OOPS_SOMETHING_WENT_WRONG);
                        }
                    });

        } else {
            AppUtil.toast(FulfillmentDetails.this, getString(R.string.check_interent_connection));
        }

        SpannableString ss = new SpannableString(" By placing a bid, you\\'re committing to fulfill delivery items if you win. I Agree to\n" +
                "        terms and conditions.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, ss.length() - 22, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        bid_licence.setHighlightColor(Color.TRANSPARENT);
        bid_licence.setText(ss);
        bid_licence.setMovementMethod(LinkMovementMethod.getInstance());
        bid_now_btn.setOnClickListener(this);
        fulfiment2_tv.setOnClickListener(this);
        fulfiment1_tv.setOnClickListener(this);

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        //  broadCast = (BroadCast) getIntent().getSerializableExtra("broadCast");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void buildUI() {
        if (broadCast == null)
            return;

        if (broadCast.Fulfillments.size() == 1) {
            listivew.setVisibility(View.GONE);
        } else if (broadCast.Fulfillments.size() == 2) {

            fulfillments_count_LI.setVisibility(View.VISIBLE);
            listivew.setVisibility(View.GONE);
            if (positon == 1) {
                fulfiment2_tv.setTextColor(Color.parseColor("#EC932F"));
                fulfiment1_tv.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                fulfiment2_tv.setTextColor(Color.parseColor("#FFFFFF"));
                fulfiment1_tv.setTextColor(Color.parseColor("#EC932F"));
            }

        } else {
            fulfillments_count_LI.setVisibility(View.GONE);
            listivew.setVisibility(View.VISIBLE);
        }

        fufillerDetailsAdapter = new FufillerDetails_Adapter(this, broadCast.Fulfillments);
        listivew.setAdapter(fufillerDetailsAdapter);

        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        companyname_tv.setText(broadCast.BusinessLegalName);
        locationtype_text_view.setText(broadCast.RetailerLocationAddress.LocationType);
        locationtype_tv.setText(broadCast.RetailerLocationAddress.LocationType + " Category");
        pickup_loc_tv.setText(broadCast.RetailerLocationAddress.RetailerLocationAddress.AddressLine1 + ", " +
                broadCast.RetailerLocationAddress.RetailerLocationAddress.City + ", " +broadCast.RetailerLocationAddress.RetailerLocationAddress.State+", "+
                broadCast.RetailerLocationAddress.RetailerLocationAddress.CountryName);


        fillfromData(positon,false);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    111);
        } else {

            Log.d("permission granted", "permisson granted");
            if (Common.isNetworkAvailable(MyApplication.getInstance()))
                if (mGPS.canGetLocation()) {
                    Log.d("user locatoinmarkers", "" + mGPS.getLatitude());
                    Marker usermarkerss = googleMap.addMarker(new MarkerOptions().position(
                            new LatLng(mGPS.getLatitude(), mGPS.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_ics)));
                    markerList.add(usermarkerss);
                }
        }

        setUpMap(broadCast);
        for (int i = 0; i < broadCast.Fulfillments.size(); i++) {
            Marker usermarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(broadCast.Fulfillments.get(i).PickUpMapLatitude,
                    broadCast.Fulfillments.get(i).PickUpMapLongitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_green_ics)));
            markerList.add(usermarker);

        }

        //lat bounds method
        latBounds();

    }

    private void latBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
            Log.d("all markers", "" + marker.getPosition().longitude + "," + marker.getPosition().latitude);
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 3.10); // offset from edges of the map 12% of screen
        CameraPosition camPos = new CameraPosition.Builder()
                .target(bounds.getCenter())
                .zoom(3)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);

        googleMap.animateCamera(cu);
    }

    private void setUpMap(BroadCast broadCast) {

        View marker = ((LayoutInflater) MyApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.name_tv);
        TextView fulfillments = (TextView) marker.findViewById(R.id.orders_tv);
        numTxt.setText(broadCast.BusinessLegalName);
        fulfillments.setText(broadCast.Fulfillments.size() + " Orders");
        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(broadCast.RetailerLocationAddress.RetailerLocationAddress.Latitude),
                        Double.parseDouble(broadCast.RetailerLocationAddress.RetailerLocationAddress.Longitude)))
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MyApplication.getInstance(), marker))));
        markerList.add(marker1);

    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        //context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }

    public void fillfromData(final int positon,boolean cancelTimer) {

        if (cancelTimer){
            if (timer != null)
                timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO do your thing
                        try {
                            Date myDate = simpleDateFormat.parse(broadCast.Fulfillments.get(positon).ExpirationDateTime);
                            Date date = new Date();
                            // System.out.println("Date  "+myDate+",current date"+date);
                            printDifference(date, myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 1000);

        // deliverytoken_tv.setText(""+broadCast.Fulfillments.get(positions).FulfillerInterests.ConfirmationCode);
        fulfillmentid_tv.setText(broadCast.Fulfillments.get(positions).FulfillmentId);
        orders_tv.setText("" + broadCast.Fulfillments.get(positon).OrderCount);
        total_weight_tv.setText(AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).TotalWeight) + " Lbs");
        mindistance_tv.setText(AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).TotalDistance) + " Miles");
        maxdistance_tv.setText(TimeUnit.SECONDS.toMinutes(Long.parseLong(broadCast.Fulfillments.get(positon).TotalApproxTimeInSeconds)) + " min");
        point_miles_tv.setText(broadCast.Fulfillments.get(positon).TotalApproxTimeInSeconds + " Miles");


        if (broadCast.Fulfillments.get(positon).PriceType.toLowerCase().contains("fixed")) {
            enter_bid_ev.setVisibility(View.GONE);
            pricetype_tv.setText("$" + AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).Amount) + " FIXED PRICE");
            price_imv.setImageResource(R.drawable.fixed_price);
            pricetype_tv.setTextColor(Color.parseColor("#89A469"));
            bid_text_tv.setText("i am interested to fulfill for " + AppUtil.getTwoDecimals(broadCast.Fulfillments.get(positon).Amount) + "$");
            bid_now_btn.setText("Notify Retailer");
            bid_licence.setText("By notifying retailer, you\'re committing to fulfill once retailer confirms your interest. I Agree to" +
                    " terms and conditions.");
        } else {
            enter_bid_ev.setVisibility(View.VISIBLE);
            enter_bid_ev.clearFocus();
            pricetype_tv.setText("BIDDING PRICE");
            pricetype_tv.setTextColor(Color.parseColor("#ED7165"));
            price_imv.setImageResource(R.drawable.bidding_price);
            bid_text_tv.setText("BID NOW");

            bid_now_btn.setText("Bid now");
            bid_licence.setText("By placing a bid, you\'re committing to fulfill delivery items if you win. I Agree to" +
                    " terms and conditions.");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fulfiment1_tv:
                fillfromData(0,true);
                fulfiment1_tv.setTextColor(Color.parseColor("#EC932F"));
                fulfiment2_tv.setTextColor(Color.parseColor("#FFFFFF"));
                positions = 0;
                break;
            case R.id.fulfiment2_tv:
                fillfromData(1,true);
                fulfiment2_tv.setTextColor(Color.parseColor("#EC932F"));
                fulfiment1_tv.setTextColor(Color.parseColor("#FFFFFF"));
                positions = 1;
                break;

            case R.id.icon_back:
                finish();
                break;
            case R.id.cus_address_LI:
                startActivity(new Intent(this, CustomerAddress.class)
                        .putExtra("broadCast", broadCast)
                        .putExtra("position", positions));
                break;
            //lat bounds method
            case R.id.current_location_tv:
                markerList.clear();
                if (mGPS.canGetLocation()) {
                    Log.d("user locatoinmarkers", "" + mGPS.getLatitude());
                    Marker usermarker = googleMap.addMarker(new MarkerOptions().position(
                            new LatLng(mGPS.getLatitude(), mGPS.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_ics)));
                    markerList.add(usermarker);
                }

                latBounds();
                break;

            case R.id.bid_now_btn:
                if (bid_licence.isChecked()) {
                    if (!enter_bid_ev.isShown()) {

                        startActivity(new Intent(this, Invoice.class)
                                .putExtra("broadCast", broadCast)
                                .putExtra("position", positions)
                                .putExtra("bidamount", 0));
                    } else {
                        if (TextUtils.isEmpty(enter_bid_ev.getText())) {
                            AppUtil.toast(FulfillmentDetails.this, "Bid Amount cant be zero");
                            return;
                        }
                        startActivity(new Intent(this, Invoice.class)
                                .putExtra("broadCast", broadCast)
                                .putExtra("position", positions)
                                .putExtra("bidamount", enter_bid_ev.getText().toString()));
                    }
                } else {
                    AlertDialogManager.showAlertOnly(this, "Fulfiller", "Please accept terms & conditions", "Ok");
                }

                break;
        }
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public void printDifference(Date startDate, Date endDate) {

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

        day_tv.setText("" + elapsedDays + " Days");
        time_tv.setText("" + elapsedHours + "H : " + elapsedMinutes + "M : " + elapsedSeconds + "S");


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("FulfillmentDetails Page") // TODO: Define a title for the content shown.
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
