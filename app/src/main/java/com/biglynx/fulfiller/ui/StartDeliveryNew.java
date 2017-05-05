package com.biglynx.fulfiller.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.MainActivity;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.MessagesAdapter;
import com.biglynx.fulfiller.adapter.StartDeliveryAdapter;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.MessagesModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.CaptureSignatureView;
import com.biglynx.fulfiller.utils.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/* 2- notified
* 3- Confirmed no.of packages count
* 4- started
* 5- delivered
* 6- Fulfillment Confirmed delivery*/
public class StartDeliveryNew extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int READ_PHONE_STATE_PERMISSION = 1;
    private static int statusId = 0;
    ImageView icon_back, help_icon, subway_arrow_imv, trackimage_imv, ontheway_imv, pickedup_imv, delivered_imv, companylogo_imv;
    TextView deliverydate_tv, confirmtoken_tv, readytopick_tv, mindistance_tv, maxdistance_tv, notifiy_btn_tv, notifieddata_tv,
            ontheway_tv, pickedup_tv, delivered_tv, confirm_deli_tv, confirm_pickup_btn_tv, day_tv, time_tv, phoneno_tv, name_tv, address_tv;
    LinearLayout subway_details_LI, details_LI, deliv_customers_LI, googlemaps_LI, confirmorders_LI;
    ProgressBar progress_bar;
    private Handler handler = new Handler();
    private float startpos;
    private int progressStatus;
    private static int deviceswidth;
    private EditText confirm_code_ev, no_ofpackages_ev, no_ofcustomers_ev;
    private RecyclerView orderlist_LI;
    private InterestDTO responseInterestObj;
    private StartDeliveryAdapter startDeliveryAdapter;
    private SimpleDateFormat simpleDateFormat;
    private TextView fulfillmentid_tv;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager, mOrdersLayoutManager;
    private MessagesAdapter adapter;
    private FullFillerApiWrapper apiWrapper;
    private EditText replyText_ev;
    private TextView reply_tv, retailerName_tv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView mScrollView;
    private SwitchCompat confirmSwitch;
    private View signatureLayout;
    private View signatureSave;
    private View signatureCancel;
    private String orderId;
    private String mCurrentPhotoPath;
    private static String responseString;
    private CaptureSignatureView signView;
    private static String blobId;
    private LinearLayout confirm_delivery_LI;
    private String FULFILLER_ID;
    private String FULFILLER_NAME;
    private TextView mCopy;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String TAG = StartDeliveryNew.class.getSimpleName();
    private Location retailerLocation;
    private Timer msgsTimer;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        // To make activity full screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_delivery);
        getpermissions();
        getScreenResolution(this);

        initViews();

        progressStatus = 0;
        if (getIntent().getExtras() != null) {
            responseInterestObj = (InterestDTO) getIntent().getExtras().getParcelable("responseInterest");
            FULFILLER_ID = getIntent().getExtras().getString("fulfillerId");
            if (getIntent().hasExtra("FulfillerName"))
                FULFILLER_NAME = getIntent().getExtras().getString("FulfillerName");
        }
        if (responseInterestObj != null) {
            retailerLocation.setLatitude(Double.parseDouble(responseInterestObj.Fulfillments.RetailerLocation.RetailerLocationAddress.Latitude));
            retailerLocation.setLongitude(Double.parseDouble(responseInterestObj.Fulfillments.RetailerLocation.RetailerLocationAddress.Longitude));
            buildUI(responseInterestObj);
            //update progressbar
            if (responseInterestObj.Fulfillments.DeliveryStatusId > 2) {
                switch (responseInterestObj.Fulfillments.DeliveryStatusId) {
                    case 3:
                    case 4:
                        updateProgressbar(50);
                        break;
                    case 5:
                        updateProgressbar(100);
                        break;
                }
            } else
                updateProgressbar(progressStatus);
            updateDeliveryScreens(responseInterestObj.Fulfillments.DeliveryStatusId);
            callGetMessagesAPI(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            if (Common.isNetworkAvailable(this)) {
                if (Common.isGpsEnabled(this)) {
                    buildGoogleApiClient();
                } else
                    Common.showCustomDialog(this);
            } else
                AppUtil.toast(getApplicationContext(), getString(R.string.check_interent_connection));
        }
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

    }

    private void getpermissions() {
        int sdkVersion = Build.VERSION.SDK_INT;

        if (sdkVersion >= 23) {
            // Getting permissins to Read from External Storage
            getPermissionsToReadPhoneState();
        }
    }

    public void getPermissionsToReadPhoneState() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and
            // denied
            // it. If so, we want to give more explanation about why the
            // permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                // Show our own UI to explain to the user why we need to read the
                // external storage
                // before requesting the permission and showing the default UI

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.permisn_denied));
                builder.setMessage(getString(R.string.explantn_for_requstng_permissn));
                builder.setPositiveButton("RE-TRY", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User has asked the permission again.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(StartDeliveryNew.this, new
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
                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(StartDeliveryNew.this, new
                                    String[]{android.Manifest.permission.READ_PHONE_STATE},
                            READ_PHONE_STATE_PERMISSION);
                    return;
                }
            }
        } else {
            //Permission already granted
        }

    }

    // Callback with the request from calling requestPermissions(...)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_PHONE_STATE_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog dialog = new AlertDialog.Builder(StartDeliveryNew.this)
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
        } else {
            // super.onRequestPermissionsResult(requestCode, permissions,
            // grantResults);
        }
    }

    //StartDeliveryNew API
    private void callStartDeliveryService(final int id) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("fulfillerid", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FulfillerId") : FULFILLER_ID);
        if (responseInterestObj.Fulfillments.DeliveryPartner != null)
            hashMap.put("confirmationcode", responseInterestObj.Fulfillments.DeliveryPartner.ConfirmationCode);
        else
            hashMap.put("confirmationcode", responseInterestObj.Fulfillments.DeliveryPerson.ConfirmationCode);
        hashMap.put("name", AppUtil.ifNotEmpty(responseInterestObj.Fulfillments.LocationContactPerson) ?
                responseInterestObj.Fulfillments.LocationContactPerson : "");
        hashMap.put("latitude", AppUtil.ifNotEmpty(String.valueOf(responseInterestObj.Fulfillments.PickUpMapLatitude)) ?
                responseInterestObj.Fulfillments.PickUpMapLatitude : "");
        hashMap.put("longitude", AppUtil.ifNotEmpty(String.valueOf(responseInterestObj.Fulfillments.PickUpMapLongitude)) ?
                responseInterestObj.Fulfillments.PickUpMapLongitude : "");

        hashMap.put("deviceid", getDeviceId());
        apiWrapper.startDeliveryCall(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                        AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("AuthNToken") : "",
                hashMap, new Callback<InterestDTO>() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(Call<InterestDTO> call, Response<InterestDTO> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            responseInterestObj = response.body();
                            retailerLocation.setLatitude(Double.parseDouble(responseInterestObj.Fulfillments.RetailerLocation.RetailerLocationAddress.Latitude));
                            retailerLocation.setLongitude(Double.parseDouble(responseInterestObj.Fulfillments.RetailerLocation.RetailerLocationAddress.Longitude));
                            int count = checkIfAllOrdersAllDelivered(responseInterestObj);
                            startDeliveryAdapter.setItemsList(responseInterestObj.Fulfillments.Orders);
                            if (id == 5 && count > 0) {
                                int progressBarBreak = 50 / responseInterestObj.Fulfillments.Orders.size();
                                if (count == responseInterestObj.Fulfillments.Orders.size())
                                    updateProgressbar(100);
                                else
                                    updateProgressbar(50 + (count * progressBarBreak));
                            }

                        } else {
                            try {
                                AppUtil.parseErrorMessage(StartDeliveryNew.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null)
                                AppUtil.CheckErrorCode(StartDeliveryNew.this, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<InterestDTO> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    public void callUpdateDeliverySignLayout(String orderId) {
        signatureLayout.setVisibility(View.VISIBLE);
        this.orderId = orderId;
    }

    public void callUpdateDeliveryStatusAPi(final int deliveryStatusId, String orderId, String blobId) {
        Log.d(StartDeliveryNew.class.getSimpleName(), "Statsu ID :: " + deliveryStatusId);
        HashMap<String, Object> hashMap = getDeliveryStatusDetails();
        hashMap.put("DeliveryStatusId", deliveryStatusId);
        if (deliveryStatusId == 4 || deliveryStatusId == 5) {
            hashMap.put("OrderId", orderId);
            if (blobId != null) {
                hashMap.put("DelSign", blobId);
            }
        }
        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        Common.showDialog(StartDeliveryNew.this);
        apiWrapper.updateDeliverYStatusCall(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                        AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("AuthNToken") : "",
                hashMap, new Callback<InterestDTO>() {
                    @Override
                    public void onResponse(Call<InterestDTO> call, Response<InterestDTO> response) {
                        if (response.isSuccessful()) {
                            switch (deliveryStatusId) {
                                case 2:
                                    //request for location updates
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                            Manifest.permission.ACCESS_FINE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED) {
                                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                                mLocationRequest, StartDeliveryNew.this);
                                    }
                                    updateProgressbar(5);
                                    break;
                                case 3:
                                    // user confirmed currect no.of packages and order count
                                    //stop location updates and update progress bar to 50(pickedup)
                                    updateProgressbar(50);
                                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                                            StartDeliveryNew.this);
                                    break;
                                case 4:
                                case 5:
                                    callStartDeliveryService(deliveryStatusId);
                                    break;
                                case 6:
                                    if (FULFILLER_ID == null)
                                        finishActivity();
                                    else {
                                        startActivity(new Intent(StartDeliveryNew.this, LoginActivity.class));
                                        finish();
                                    }
                                    break;
                            }

                            updateDeliveryScreens(deliveryStatusId);

                        } else {
                            try {
                                AppUtil.parseErrorMessage(StartDeliveryNew.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null)
                                AppUtil.CheckErrorCode(StartDeliveryNew.this, response.code());
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<InterestDTO> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    public void callUpdateDeliveryStatusAPi(final int deliveryStatusId, String orderId) {
        callUpdateDeliveryStatusAPi(deliveryStatusId, orderId, null);
    }

    public HashMap<String, Object> getDeliveryStatusDetails() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("fulfillerid", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FulfillerId")
                : FULFILLER_ID);
        if (responseInterestObj.Fulfillments.DeliveryPartner != null)
            hashMap.put("confirmationcode", responseInterestObj.Fulfillments.DeliveryPartner.ConfirmationCode);
        else
            hashMap.put("confirmationcode", responseInterestObj.Fulfillments.DeliveryPerson.ConfirmationCode);

        if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null) {
            if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("Role").equals("DeliveryPartner")) {
                if (!TextUtils.isEmpty(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("BusinessLegalName")))
                    hashMap.put("FriendlyName", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("BusinessLegalName"));
            } else {
                if (!TextUtils.isEmpty(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FirstName")))
                    hashMap.put("FriendlyName", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FirstName"));
            }
        } else
            hashMap.put("FriendlyName", FULFILLER_NAME);
        hashMap.put("GeoLocationLatitude", AppUtil.ifNotEmpty(String.valueOf(responseInterestObj.Fulfillments.PickUpMapLatitude)) ?
                responseInterestObj.Fulfillments.PickUpMapLatitude : "");
        hashMap.put("GeoLocationLongitude", AppUtil.ifNotEmpty(String.valueOf(responseInterestObj.Fulfillments.PickUpMapLongitude)) ?
                responseInterestObj.Fulfillments.PickUpMapLongitude : "");
        hashMap.put("UniqueDeviceId", getDeviceId());
        hashMap.put("DeliveryStatusId", "");
        hashMap.put("OrderId", "");
        hashMap.put("DelSign", "");

        return hashMap;
    }

    private Object getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = telephonyManager.getDeviceId();
        Log.d(StartDeliveryNew.class.getSimpleName(), "Device ID :: " + deviceID);
        return deviceID;
    }

    private void initViews() {
        msgsTimer = new Timer();
        runMessagesTimer();
        apiWrapper = new FullFillerApiWrapper();
        retailerLocation = new Location("locationA");

        icon_back = (ImageView) findViewById(R.id.icon_back);
        help_icon = (ImageView) findViewById(R.id.help_icon);
        subway_arrow_imv = (ImageView) findViewById(R.id.subway_arrow_imv);
        trackimage_imv = (ImageView) findViewById(R.id.trackimage_imv);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //live status update initilizations
        ontheway_imv = (ImageView) findViewById(R.id.ontheway_imv);
        pickedup_imv = (ImageView) findViewById(R.id.pickedup_imv);
        delivered_imv = (ImageView) findViewById(R.id.delivered_imv);
        companylogo_imv = (ImageView) findViewById(R.id.companylogo_imv);
        ontheway_tv = (TextView) findViewById(R.id.ontheway_tv);
        pickedup_tv = (TextView) findViewById(R.id.pickedup_tv);
        delivered_tv = (TextView) findViewById(R.id.delivered_tv);

        //confirm delivery intilizations
        confirm_pickup_btn_tv = (TextView) findViewById(R.id.confirm_pickup_btn_tv);
        no_ofpackages_ev = (EditText) findViewById(R.id.no_ofpackages_ev);
        no_ofcustomers_ev = (EditText) findViewById(R.id.no_ofcustomers_ev);
        day_tv = (TextView) findViewById(R.id.day_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);
        //orders to customer
        confirm_deli_tv = (TextView) findViewById(R.id.confirm_deli_tv);
        confirm_delivery_LI = (LinearLayout) findViewById(R.id.confirm_delivery_LI);

        phoneno_tv = (TextView) findViewById(R.id.phoneno_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);
        address_tv = (TextView) findViewById(R.id.address_tv);
        mCopy = (TextView) findViewById(R.id.copy_retailer_adrs_tv);
        mCopy.setOnClickListener(this);
        confirm_code_ev = (EditText) findViewById(R.id.confirm_code_ev);

        startpos = trackimage_imv.getX();
        subway_details_LI = (LinearLayout) findViewById(R.id.subway_details_LI);
        details_LI = (LinearLayout) findViewById(R.id.details_LI);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setProgress(100);

        deliv_customers_LI = (LinearLayout) findViewById(R.id.deliv_customers_LI);
        googlemaps_LI = (LinearLayout) findViewById(R.id.googlemaps_LI);
        confirmorders_LI = (LinearLayout) findViewById(R.id.confirmorders_LI);

        deliverydate_tv = (TextView) findViewById(R.id.deliverydate_tv);
        confirmtoken_tv = (TextView) findViewById(R.id.confirmtoken_tv);
        readytopick_tv = (TextView) findViewById(R.id.readytopick_tv);
        mindistance_tv = (TextView) findViewById(R.id.mindistance_tv);
        maxdistance_tv = (TextView) findViewById(R.id.maxdistance_tv);
        notifiy_btn_tv = (TextView) findViewById(R.id.notifiy_btn_tv);
        notifieddata_tv = (TextView) findViewById(R.id.notifieddata_tv);
        fulfillmentid_tv = (TextView) findViewById(R.id.fulfillmentid_tv);
        replyText_ev = (EditText) findViewById(R.id.replyText_ev);
        reply_tv = (TextView) findViewById(R.id.reply_tv);
        retailerName_tv = (TextView) findViewById(R.id.retailerName_tv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_To_Refresh_Layout);
        mScrollView = (ScrollView) findViewById(R.id.deliveryScrollView);
        confirmSwitch = (SwitchCompat) findViewById(R.id.confirmSwitch);

        signatureLayout = findViewById(R.id.signature_layout);
        signatureSave = findViewById(R.id.signature_save);
        signatureCancel = findViewById(R.id.signature_cancel);
        signView = (CaptureSignatureView) findViewById(R.id.signature_view);
        signatureCancel.setOnClickListener(this);
        signatureSave.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.messages_listView);
        mLayoutManager = new LinearLayoutManager(StartDeliveryNew.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new MessagesAdapter(StartDeliveryNew.this);
        mRecyclerView.setAdapter(adapter);

        orderlist_LI = (RecyclerView) findViewById(R.id.orderlist_LI);
        orderlist_LI.setHasFixedSize(true);
        mOrdersLayoutManager = new LinearLayoutManager(this);
        orderlist_LI.setLayoutManager(mOrdersLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST);
        orderlist_LI.addItemDecoration(dividerItemDecoration);
        startDeliveryAdapter = new StartDeliveryAdapter(this);
        orderlist_LI.setAdapter(startDeliveryAdapter);

        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        help_icon.setOnClickListener(this);
        subway_arrow_imv.setOnClickListener(this);
        notifiy_btn_tv.setOnClickListener(this);
        confirm_deli_tv.setOnClickListener(this);
        confirm_pickup_btn_tv.setOnClickListener(this);
        reply_tv.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void runMessagesTimer() {
        msgsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "MessagesApi :: " + System.currentTimeMillis());
                        callGetMessagesAPI(false);
                    }
                });
            }
        }, 0, 6000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msgsTimer != null)
            msgsTimer.cancel();
    }

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        deviceswidth = width - 100;
        return "{" + width + "," + height + "}";
    }

    public void updateDeliveryScreens(int deliveryStatusId) {
        if (deliveryStatusId == 1) {
            if (!googlemaps_LI.isShown())
                googlemaps_LI.setVisibility(View.VISIBLE);

        } else if (deliveryStatusId == 2) {
            //user Notified the retailer that he is comming to pickup the fulfillment
            //so show confirm packages UI
            googlemaps_LI.setVisibility(View.GONE);
            confirmorders_LI.setVisibility(View.VISIBLE);
        } else if (deliveryStatusId == 3 || deliveryStatusId == 4 || deliveryStatusId == 5) {
            googlemaps_LI.setVisibility(View.GONE);
            confirmorders_LI.setVisibility(View.GONE);
            deliv_customers_LI.setVisibility(View.VISIBLE);
            checkIfAllOrdersAllDelivered(responseInterestObj);
        } else if (deliveryStatusId == 6) {
            delivered_imv.setImageResource(R.drawable.ic_delivered_grn_n);
            delivered_tv.setTextColor(Color.parseColor("#94C96F"));
        }
    }

    private void updateProgressbar(final int max) {

        // Start the lengthy operation in a background thread
        progress_bar.setProgress(progressStatus);

        Log.d("intial progressbar", startpos + "," + progressStatus);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < max) {
                    // Update the progress status
                    progressStatus += 1;

                    // Try to sleep the thread for 20 milliseconds
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress_bar.setProgress(progressStatus);
                            trackimage_imv.setX(progressStatus * (deviceswidth / 100));
                            if (progressStatus == 5) {
                                ontheway_imv.setImageResource(R.drawable.ic_onthe_way_grn_n);
                                ontheway_tv.setTextColor(Color.parseColor("#94C96F"));
                            }
                            if (progressStatus == 50) {
                                pickedup_imv.setImageResource(R.drawable.ic_pickedup_grn_n);
                                pickedup_tv.setTextColor(Color.parseColor("#94C96F"));
                            }


                            if (progressStatus == 100) {
                                delivered_imv.setImageResource(R.drawable.ic_delivered_grn_n);
                                delivered_tv.setTextColor(Color.parseColor("#94C96F"));
                            }
                        }
                    });
                }
            }
        }).start(); // Start the operation
    }

    /**
     * Creating the bitmap and get
     *
     * @param
     */
    private void createImageFile(Bitmap bitmap) throws IOException {
        // Create an image file name
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        FileOutputStream stream = new FileOutputStream(image);
        stream.write(bytes.toByteArray());
        stream.close();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();

        new SendImageResult().execute(mCurrentPhotoPath);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentLocation != null && location != null &&
                (mCurrentLocation.getLatitude() == location.getLatitude() &&
                        mCurrentLocation.getLongitude() == location.getLongitude())) {
            return;
        }

        //user notified and then we request for location updates
        //user notifeis means started...started position is 5 on progressBar
        double speed = 5;
        if (location.hasSpeed())
            speed = location.getSpeed();
        else {
            if (mCurrentLocation != null) {
                double elapsedTime = (location.getTime() - mCurrentLocation.getTime()) / 1000; // Convert milliseconds to seconds
                speed = mCurrentLocation.distanceTo(location) / elapsedTime;
            }
        }

        mCurrentLocation = location;
        if (mCurrentLocation != null) {
            calculateDistance(speed);
        }
        Log.d(TAG, mCurrentLocation.getLatitude() + " -- " + mCurrentLocation.getLongitude());
    }

    private void calculateDistance(double speed) {
        double distance = distanceCalculate();
        //distance = speed * time
        if (speed == 0)
            return;
        //50 indicates- calculate distance upto pickup. Pickup is the position
        //where retailer stays and user pickups the fulfillment here.
        Double time = new Double(50 / (distance / speed));
        int progressTime = time.intValue();
        updateProgressbar(progressTime);
    }

    private double distanceCalculate() {
        double distance = 0;
        distance = retailerLocation.distanceTo(mCurrentLocation);
        return distance;
    }


    public class SendImageResult extends AsyncTask<String, String, String> {
        URL url = null;
        String responce = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.showDialog(StartDeliveryNew.this);
        }

        @Override
        protected String doInBackground(String... arg0) {
            uploadUserPhoto(arg0[0]);

            return responce;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            Common.disMissDialog();
            //  finish();
            try {
                if (responseString == null || responseString.length() == 0) {
                    AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                    return;
                }
                JSONArray result = new JSONArray(responseString);
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jsonObject = result.getJSONObject(i);
                        blobId = jsonObject.getString("UniqueId");
                        if (orderId != null) {
                            callUpdateDeliveryStatusAPi(5, orderId, blobId);
                        }
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void uploadUserPhoto(String filepath) {

        try {
            Log.d("file path", "" + filepath);

            File file = new File(filepath);
            HttpPost httppost = new HttpPost("https://www.eyece.com/Services/Api/FileSystem/UploadPublic?extension=png");
            String boundary = "----" + System.currentTimeMillis() + "----";
            // httppost.setHeader("Content-type", "multipart/form-data; boundary=" + boundary);
            org.apache.http.entity.mime.MultipartEntity multipartEntity = new org.apache.http.entity.mime.MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            // httppost.containsHeader(lineEnd + twoHyphens + boundary + lineEnd);
            // multipartEntity.addPart("filepath", new StringBody(""));
            //  httppost.containsHeader(lineEnd + twoHyphens + boundary + lineEnd);
            multipartEntity.addPart("image", new FileBody(file, "image/jpg"));
            //  httppost.setHeader("Accept", "application/json");
            // httppost.containsHeader(lineEnd);
            httppost.setEntity(multipartEntity);
            //Log.d("entity",""+multipartEntity.getContent());
            HttpParams params = new BasicHttpParams();

            // params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            //                System.out.println("executing request " + multipartEntity.getContent());
            HttpClient httpClient = new DefaultHttpClient();
            try {
                httpClient.execute(httppost, new PhotoUploadResponseHandler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("data", e.getLocalizedMessage(), e);
        }
    }

    private class PhotoUploadResponseHandler implements ResponseHandler<Object> {

        @Override
        public Object handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            //10-08 03:19:13.966: E/res(27552): File Uploaded Successfully...
            // Log.e("jsonObject res", ""+response);
            HttpEntity r_entity = response.getEntity();
            responseString = EntityUtils.toString(r_entity);
            Log.e("jsonObject res", "" + responseString);


            return null;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signature_save:
                // First image save and get the blobID and send the blobID to deliveryStatus API
                if (signatureLayout.isShown())
                    signatureLayout.setVisibility(View.GONE);
                if (signView != null) {
                    Bitmap bitmap = signView.getBitmap();
                    if (bitmap != null) {
                        try {
                            createImageFile(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.signature_cancel:
                if (signatureLayout.isShown()) {
                    signatureLayout.setVisibility(View.GONE);
                }
                startDeliveryAdapter.notifyDataSetChanged();
                orderId = null;
                break;
            case R.id.icon_back:
                finish();
                break;

            case R.id.help_icon:
                startActivity(new Intent(StartDeliveryNew.this, CustomerSupport.class));
                break;

            case R.id.confirm_deli_tv:
                if (confirm_code_ev.getText().toString().equalsIgnoreCase("CONFIRMED")) {
                 /* googlemaps_LI.setVisibility(View.GONE);
                  confirmorders_LI.setVisibility(View.GONE);
                  deliv_customers_LI.setVisibility(View.VISIBLE);*/
                    callUpdateDeliveryStatusAPi(6, null);
                } else {
                    AppUtil.toast(StartDeliveryNew.this, "Please type CONFIRMED");
                }
                break;
            case R.id.confirm_pickup_btn_tv:

                if (AppUtil.ifNotEmpty(no_ofcustomers_ev.getText().toString()) &&
                        AppUtil.ifNotEmpty(no_ofpackages_ev.getText().toString()) &&
                        confirmSwitch.isChecked()
                        ) {
                    //callUpdateDeliveryStatusAPi(3, null);

                    if (no_ofpackages_ev.getText().toString().trim().equals(responseInterestObj.Fulfillments.TotalPackages) &&
                            no_ofcustomers_ev.getText().toString().trim().equals(responseInterestObj.Fulfillments.OrderCount)) {
                        Log.d(StartDeliveryNew.class.getSimpleName(), "" + "no_ofpackages_ev :: " + no_ofpackages_ev.getText().toString() + " :: " +
                                "no_ofcustomers_ev :: " + no_ofcustomers_ev.getText().toString());
                        callUpdateDeliveryStatusAPi(3, null);
                    } else
                        AppUtil.toast(StartDeliveryNew.this, getString(R.string.not_matched));
                } else
                    AppUtil.toast(StartDeliveryNew.this, getString(R.string.mandatory));
                break;

            case R.id.notifiy_btn_tv:
                callUpdateDeliveryStatusAPi(2, null);
                break;
            case R.id.subway_arrow_imv:
                if (details_LI.isShown()) {
                    details_LI.setVisibility(View.GONE);
                    subway_arrow_imv.setImageResource(R.drawable.down_white_arrow);
                } else {
                    subway_arrow_imv.setImageResource(R.drawable.up_white_arrow);
                    details_LI.setVisibility(View.VISIBLE);
                    mScrollView.smoothScrollTo((int) details_LI.getX(), (int) details_LI.getY());
                }
                break;
            case R.id.reply_tv:
                if (!TextUtils.isEmpty(replyText_ev.getText().toString())) {
                    callPostMessagesAPI(replyText_ev.getText().toString());
                    replyText_ev.setText("");
                    replyText_ev.setHint(getString(R.string.send_msg_to_retailer));
                } else {
                    AppUtil.toast(StartDeliveryNew.this, getString(R.string.message_cant_be_empty));
                }
                break;
            case R.id.copy_retailer_adrs_tv:
                AppUtil.copyText(getApplicationContext(), "Address", address_tv.getText().toString());
                break;
        }
    }

    private void callPostMessagesAPI(String msg) {
        if (!Common.isNetworkAvailable(MyApplication.getInstance())) {
            AppUtil.toast(StartDeliveryNew.this, "Network disconnected. Please check");
            return;
        }

        Common.showDialog(StartDeliveryNew.this);
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("FulfillmentId", responseInterestObj.Fulfillments.FulfillmentId);
        hashMap.put("fulfillerid", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FulfillerId")
                : FULFILLER_ID);
        if (FULFILLER_NAME != null)
            hashMap.put("FlulfillerName", FULFILLER_NAME);
        else if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null) {
           /* if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("Role").equals("DeliveryPartner")) {
                if (!TextUtils.isEmpty(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("BusinessLegalName")))
                    hashMap.put("FlulfillerName", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("BusinessLegalName"));
            } else {*/
            if (!TextUtils.isEmpty(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FirstName")))
                hashMap.put("FlulfillerName", AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("FirstName"));
            //}
        } else {
            if (responseInterestObj.Fulfillments.DeliveryPartner != null)
                hashMap.put("FlulfillerName", responseInterestObj.Fulfillments.DeliveryPartner.Contactperson);
            else
                hashMap.put("FlulfillerName", responseInterestObj.Fulfillments.DeliveryPerson.Contactperson);
        }
        hashMap.put("RetailerId", "");
        hashMap.put("RetailerName", "");
        hashMap.put("MessageFrom", "Fulfiller");
        hashMap.put("Message", msg.trim());

        apiWrapper.postFulfillerMessagesCall(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                        AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("AuthNToken") : "",
                hashMap, new Callback<List<MessagesModel>>() {
                    @Override
                    public void onResponse(Call<List<MessagesModel>> call, Response<List<MessagesModel>> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            if (!mRecyclerView.isShown())
                                mRecyclerView.setVisibility(View.VISIBLE);
                            MessagesModel messagesModel = new MessagesModel();
                            messagesModel.FlulfillerName = (String) hashMap.get("FlulfillerName");
                            messagesModel.Message = (String) hashMap.get("Message");
                            adapter.getModelArrayList().add(messagesModel);
                            adapter.notifyDataSetChanged();
                        } else {
                            if (mRecyclerView.isShown())
                                mRecyclerView.setVisibility(View.GONE);
                            try {
                                AppUtil.parseErrorMessage(StartDeliveryNew.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null)
                                AppUtil.CheckErrorCode(StartDeliveryNew.this, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MessagesModel>> call, Throwable t) {
                        Common.disMissDialog();
                        if (mRecyclerView.isShown())
                            mRecyclerView.setVisibility(View.GONE);
                        AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    private void callGetMessagesAPI(final boolean showProgress) {
        //Messages API - GET
        if (!Common.isNetworkAvailable(MyApplication.getInstance())) {
            AppUtil.toast(StartDeliveryNew.this, "Network disconnected. Please check");
            return;
        }
        if (showProgress)
            Common.showDialog(StartDeliveryNew.this);

        String fulfillmentId;
        if (responseInterestObj != null)
            fulfillmentId = responseInterestObj.Fulfillments.FulfillmentId;
        else
            fulfillmentId = "";

        apiWrapper.getAllMessagesCall(AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null ?
                        AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult().optString("AuthNToken") : "",
                fulfillmentId, new Callback<List<MessagesModel>>() {
                    @Override
                    public void onResponse(Call<List<MessagesModel>> call, Response<List<MessagesModel>> response) {
                        if (response.isSuccessful()) {
                            adapter.setItems(response.body());
                            if (!mRecyclerView.isShown())
                                mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            if (mRecyclerView.isShown())
                                mRecyclerView.setVisibility(View.GONE);
                            try {
                                AppUtil.parseErrorMessage(StartDeliveryNew.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                            }
                            if (AppPreferences.getInstance(StartDeliveryNew.this).getSignInResult() != null)
                                AppUtil.CheckErrorCode(StartDeliveryNew.this, response.code());
                        }

                        if (showProgress)
                            Common.disMissDialog();
                        else {
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }

                    }

                    @Override
                    public void onFailure(Call<List<MessagesModel>> call, Throwable t) {
                        if (showProgress)
                            Common.disMissDialog();
                        else {
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                        if (mRecyclerView.isShown())
                            mRecyclerView.setVisibility(View.GONE);
                        if (!(t instanceof EOFException))
                            AppUtil.toast(StartDeliveryNew.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildUI(InterestDTO mInterest) {

        if (AppUtil.ifNotEmpty(mInterest.Fulfillments.RetailerLocation.Retailer.CompanyLogo)) {
            if (!companylogo_imv.isShown())
                companylogo_imv.setVisibility(View.VISIBLE);
            retailerName_tv.setVisibility(View.GONE);
            Picasso.with(StartDeliveryNew.this).load(mInterest.Fulfillments.RetailerLocation.Retailer.CompanyLogo)
                    .error(R.drawable.ic_your_company_logo).into(companylogo_imv);
        } else {
            companylogo_imv.setVisibility(View.GONE);
            retailerName_tv.setVisibility(View.VISIBLE);
            retailerName_tv.setText(mInterest.Fulfillments.RetailerLocation.Retailer.BusinessLegalName);
        }

        checkIfAllOrdersAllDelivered(mInterest);

        fulfillmentid_tv.setText(mInterest.Fulfillments.FulfillmentId);

        if (responseInterestObj.Fulfillments.DeliveryPartner != null)
            confirmtoken_tv.setText(responseInterestObj.Fulfillments.DeliveryPartner.ConfirmationCode);
        else
            confirmtoken_tv.setText(responseInterestObj.Fulfillments.DeliveryPerson.ConfirmationCode);

        readytopick_tv.setText("YES");
        mindistance_tv.setText(AppUtil.getTwoDecimals(mInterest.Fulfillments.TotalDistance) + " Miles");
        maxdistance_tv.setText(TimeUnit.SECONDS.toMinutes(Long.parseLong(responseInterestObj.Fulfillments.TotalApproxTimeInSeconds)) + " Min");
        deliverydate_tv.setText(AppUtil.getLocalDateFormat(mInterest.Fulfillments.ExpirationDateTime));

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(mInterest.Fulfillments.RetailerLocation.LocationContactPhone, "US");
            String usFormatNUmber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            String finalNumber = usFormatNUmber.split(" ")[1];
            Log.d(StartDeliveryNew.class.getSimpleName(), "usFormatNUmber :: " + usFormatNUmber);
            Log.d(StartDeliveryNew.class.getSimpleName(), "finalNumber :: " + finalNumber);
            phoneno_tv.setText(finalNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
            phoneno_tv.setText(mInterest.Fulfillments.RetailerLocation.LocationContactPhone);
        }

        name_tv.setText(mInterest.Fulfillments.RetailerLocation.LocationContactPerson);
        address_tv.setText(mInterest.Fulfillments.RetailerLocation.RetailerLocationAddress.AddressLine1 + ", " +
                mInterest.Fulfillments.RetailerLocation.RetailerLocationAddress.City + ", " +
                mInterest.Fulfillments.RetailerLocation.RetailerLocationAddress.State + " " +
                mInterest.Fulfillments.RetailerLocation.RetailerLocationAddress.CountryName
        );

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Date myDate = simpleDateFormat.parse(responseInterestObj.Fulfillments.ExpirationDateTime);
                            Date date = new Date();
                            System.out.println("service Date  " + myDate + ",current date" + date);
                            printDifference(date, myDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 1000);
        startDeliveryAdapter.setItemsList(mInterest.Fulfillments.Orders);
        //Common.setListViewHeightBasedOnItems(orderlist_LI);
    }

    private int checkIfAllOrdersAllDelivered(InterestDTO mInterest) {
        int deliveredItemsCount = 0;
        int noOfOrders = 0;
        if (deliv_customers_LI.getVisibility() == View.VISIBLE){
            if (mInterest.Fulfillments.Orders != null && responseInterestObj.Fulfillments.Orders.size() > 0) {
                noOfOrders = mInterest.Fulfillments.Orders.size();
                for (int i = 0; i < mInterest.Fulfillments.Orders.size(); i++) {
                    if (mInterest.Fulfillments.Orders.get(i).Status.equalsIgnoreCase("Delivered"))
                        deliveredItemsCount = deliveredItemsCount + 1;
                }
            }
            if (deliveredItemsCount != 0 && noOfOrders == deliveredItemsCount) {
                confirm_delivery_LI.setVisibility(View.VISIBLE);
            } else
                confirm_delivery_LI.setVisibility(View.GONE);
        }
        return deliveredItemsCount;


        /*if (deliv_customers_LI.isShown()) {
            if (mInterest.Fulfillments.Orders != null && responseInterestObj.Fulfillments.Orders.size() > 0) {
                int noOfOrders = mInterest.Fulfillments.Orders.size();
                for (int i = 0; i < mInterest.Fulfillments.Orders.size(); i++) {
                    if (mInterest.Fulfillments.Orders.get(i).Status.equalsIgnoreCase("Delivered"))
                        deliveredItemsCount = deliveredItemsCount + 1;
                }
                if (noOfOrders == deliveredItemsCount) {
                    confirm_delivery_LI.setVisibility(View.VISIBLE);
                } else
                    confirm_delivery_LI.setVisibility(View.GONE);
                return deliveredItemsCount;
            }
        }
        return 0;*/
    }

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

    @Override
    public void onRefresh() {
        callGetMessagesAPI(false);
    }

    public void finishActivity() {
        Intent intent = new Intent(StartDeliveryNew.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(HomeFragment.FULFLMNT_DELIVERED, "Delivered");
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}
