package com.biglynx.fulfiller.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.MainActivity;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.biglynx.fulfiller.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/**
 * Created by Biglynx on 7/20/2016.
 */

public class LoginActivity extends FragmentActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;
    GoogleApiClient google_api_client;
    TextView googlelogin_tv;
    TextView fb_login_tv;
    private CallbackManager callbackManager;
    SharedPreferences userDetails_shardP;
    Button start_delivery_btn;
    private EditText emailEdit;
    private EditText passwordEdit;
    private View startDeliveringLayout;
    private FullFillerApiWrapper fillerApiWrapper;
    private GoogleApiClient mGoogleApiClient;
    private TextView mTv_closeDeliveringLayout;
    private Animation animation_slide_down;
    private Animation animation_slide_up;
    private ImageView loginBgImageView;
    private TextView joinNow_tv;
    private EditText et_FullfillerId, et_confirmationCode, et_name;
    private Button bt_startDeliverywithOutLogin;
    private String TAG = LoginActivity.class.getSimpleName();
    private int LOCATION_PERMISSION = 1;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private double longitude;
    private double latitude;
    private View loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // To make activity full screen.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginactivity_new);

        //Generating the KeyHash
        generateFBkeyHash();
        initViews();
        initializeFacebookLogin();


        //Closing start Delivery layout.
        mTv_closeDeliveringLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (mTv_closeDeliveringLayout.getRight() -
                            mTv_closeDeliveringLayout.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (startDeliveringLayout.isShown()) {
                            startDeliveringLayout.startAnimation(animation_slide_down);
                            startDeliveringLayout.setVisibility(View.GONE);
                            enableDisableView(loginLayout,true);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationManager == null)
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showCustomDialog();
        else
            initialGoogleLogin();
    }

    private void showCustomDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable to proceed further.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            Log.e(TAG, "Latitude :: " + mCurrentLocation.getLatitude() + " Longitute :: " + mCurrentLocation.getLongitude());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LoginActivity.this);
        } else {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                        LoginActivity.this);
            else*/
            checkLocationPermission();
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and
            // denied
            // it. If so, we want to give more explanation about why the
            // permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to read the
                // external storage
                // before requesting the permission and showing the default UI

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.permisn_denied));
                builder.setMessage(getString(R.string.explantn_for_requstng_location_permissn));
                builder.setPositiveButton("ACCPET", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User has asked the permission again.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(LoginActivity.this, new
                                                    String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            LOCATION_PERMISSION);
                                }
                            }
                        });
                builder.setNegativeButton("DENY", new
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
                    ActivityCompat.requestPermissions(LoginActivity.this, new
                                    String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);
                    return;
                }
            }
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                    LoginActivity.this);
    }

    // Callback with the request from calling requestPermissions(...)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                if (mLocationRequest != null)
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                        LoginActivity.this);
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void generateFBkeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.biglynx.fulfiller",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void initialGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth2_client_id))
                .requestEmail()
                .build();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void initViews() {
        emailEdit = (EditText) findViewById(R.id.email_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        Button signInEmailButton = (Button) findViewById(R.id.sign_in_email);
        startDeliveringLayout = findViewById(R.id.start_delivering_layout);
        loginLayout = findViewById(R.id.login_layout);
        mTv_closeDeliveringLayout = (TextView) findViewById(R.id.close_delivering_layout);
        animation_slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slid_down);
        animation_slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slid_up);
        et_FullfillerId = (EditText) findViewById(R.id.fullfiller_id_edit);
        et_confirmationCode = (EditText) findViewById(R.id.confirmation_edit);
        et_name = (EditText) findViewById(R.id.name_edit);
        bt_startDeliverywithOutLogin = (Button) findViewById(R.id.start_delivery_without_login);
        loginBgImageView = (ImageView) findViewById(R.id.login_image);
        Picasso.with(LoginActivity.this).load(R.drawable.login_bg).error((int) R.color.colorPrimary).into(loginBgImageView);
        this.userDetails_shardP = getSharedPreferences("Userdetails", 0);
        fb_login_tv = (TextView) findViewById(R.id.login_button);
        googlelogin_tv = (TextView) findViewById(R.id.sign_in_button);
        start_delivery_btn = (Button) findViewById(R.id.start_delivery_btn);
        joinNow_tv = (TextView) findViewById(R.id.joinNow_tv);
        fillerApiWrapper = new FullFillerApiWrapper();

        bt_startDeliverywithOutLogin.setOnClickListener(this);
        signInEmailButton.setOnClickListener(this);
        joinNow_tv.setOnClickListener(this);
        fb_login_tv.setOnClickListener(this);
        googlelogin_tv.setOnClickListener(this);
        start_delivery_btn.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == RC_SIGN_IN) {
            request_code = requestCode;
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            if (!Common.isNetworkAvailable(LoginActivity.this)) {
                AppUtil.toast(LoginActivity.this, getString(R.string.check_interent_connection));
                return;
            }

            Common.showDialog(LoginActivity.this);
            fillerApiWrapper.socialLogin(Constants.GOOGLE_LOGIN, acct.getId(), new Callback<SignInResult>() {
                @Override
                public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                    Common.disMissDialog();
                    if (response.isSuccessful()) {
                        SignInResult signInResult = response.body();
                        if (signInResult != null) {
                            AppPreferences.getInstance(LoginActivity.this).setSignInResult(signInResult);
                            finishActivity();
                        } else {
                            AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                        }
                    } else {
                        try {
                            AppUtil.parseErrorMessage(LoginActivity.this, response.errorBody().string());
                        } catch (IOException e) {
                            AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                            e.printStackTrace();
                        }
                    }
                    clearGoogleAccount();
                }

                @Override
                public void onFailure(Call<SignInResult> call, Throwable t) {
                    Common.disMissDialog();
                    AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                }
            });
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void clearGoogleAccount() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {

//                    mGoogleApiClient.disconnect();
                }
            });

        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        //google_api_client.connect();
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }

    }

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    /**
     * Sign-in into the Google + account
     */
    private void gPlusSignIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (!connectionResult.hasResolution()) {
            return;
        }
        connection_result = connectionResult;

        if (is_signInBtn_clicked) {

            resolveSignInError();
        }
    }


    public void initializeFacebookLogin() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        if (object == null) {
                                            AppUtil.toast(LoginActivity.this, Constants.OOPS_SOMETHING_WENT_WRONG);
                                            return;
                                        }
                                        Log.e("fb email", "email is " + object.toString());
                                        try {
                                            Common.showDialog(LoginActivity.this);
                                            fillerApiWrapper.socialLogin(Constants.FACEBOOK_LOGIN, object.getString("id"), new Callback<SignInResult>() {
                                                @Override
                                                public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                                                    Common.disMissDialog();
                                                    if (response.isSuccessful()) {
                                                        SignInResult signInResult = response.body();
                                                        if (signInResult != null) {
                                                            AppPreferences.getInstance(LoginActivity.this).setSignInResult(signInResult);
                                                            finishActivity();
                                                        } else {
                                                            AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                                        }
                                                    } else {
                                                        try {
                                                            AppUtil.parseErrorMessage(LoginActivity.this, response.errorBody().string());
                                                        } catch (IOException e) {
                                                            AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<SignInResult> call, Throwable t) {
                                                    Common.disMissDialog();
                                                    AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                        LoginManager.getInstance().logOut();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void onFacebookLoginClick() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile,email"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_email:
                if (TextUtils.isEmpty(emailEdit.getText())) {
                    AppUtil.toast(this, "Email cannot be empty!!");
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText().toString()).matches()) {
                    AppUtil.toast(this, "Email is not valid!!");
                    return;
                }
                if (TextUtils.isEmpty(passwordEdit.getText())) {
                    AppUtil.toast(this, "Password cannot be empty!!");
                    return;
                }

                if (!Common.isNetworkAvailable(LoginActivity.this)) {
                    AppUtil.toast(LoginActivity.this, getString(R.string.check_interent_connection));
                    return;
                }

                Common.showDialog(LoginActivity.this);
                fillerApiWrapper.login(emailEdit.getText().toString(), passwordEdit.getText().toString(), new Callback<SignInResult>() {
                    @Override
                    public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                        if (response != null && !TextUtils.isEmpty(response.message()))
                            Log.d(LoginActivity.class.getSimpleName(), "Response Msg :: " + response.message());
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            SignInResult signInResult = response.body();
                            if (signInResult != null) {
                                Log.d(LoginActivity.class.getSimpleName(), "AuthToken :: " + signInResult.AuthNToken);
                                AppPreferences.getInstance(LoginActivity.this).setSignInResult(signInResult);
                                finishActivity();
                            } else {
                                AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                            }
                        } else {
                            try {
                                AppUtil.parseErrorMessage(LoginActivity.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInResult> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
                break;
            case R.id.login_button:
                // Callback registration
                if (Common.isNetworkAvailable(this)) {
                    onFacebookLoginClick();
                } else
                    AppUtil.toast(LoginActivity.this, getString(R.string.check_interent_connection));
                break;
            case R.id.sign_in_button:
                if (Common.isNetworkAvailable(this)) {
                    is_signInBtn_clicked = true;
                    gPlusSignIn();
                } else
                    AppUtil.toast(LoginActivity.this, getString(R.string.check_interent_connection));

                break;
            case R.id.start_delivery_btn:
                if (!startDeliveringLayout.isShown()) {
                    startDeliveringLayout.startAnimation(animation_slide_up);
                    startDeliveringLayout.setVisibility(View.VISIBLE);
                    enableDisableView(loginLayout,false);
                } else {
                    startDeliveringLayout.startAnimation(animation_slide_down);
                    startDeliveringLayout.setVisibility(View.GONE);
                    enableDisableView(loginLayout,true);
                }
                break;
            case R.id.joinNow_tv:
                startActivity(new Intent(LoginActivity.this, InitialScreen.class));
                break;
            case R.id.start_delivery_without_login:
                if (AppUtil.ifNotEmpty(et_FullfillerId.getText().toString()) &&
                        AppUtil.ifNotEmpty(et_confirmationCode.getText().toString()) &&
                        AppUtil.ifNotEmpty(et_name.getText().toString())) {

                    callStartDeliveryService();
                    /*startActivity(new Intent(LoginActivity.this, StartDelivery.class)
                            .putExtra("fulfillerid", et_FullfillerId.getText().toString())
                            .putExtra("confirmationcode", et_confirmationCode.getText().toString())
                            .putExtra("name", et_name.getText().toString())
                            .putExtra("latitude", latitude != 0 ? latitude : "")//47.6062
                            .putExtra("longitude", longitude != 0 ? longitude : "")//122.3321
                    );*/
                } else
                    AppUtil.toast(LoginActivity.this, getString(R.string.mandatory));
                break;
        }
    }

    private void callStartDeliveryService() {
        Common.showDialog(LoginActivity.this);
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fulfillerid", et_FullfillerId.getText().toString());
        hashMap.put("confirmationcode",et_confirmationCode.getText().toString());
        hashMap.put("name", et_name.getText().toString());
        hashMap.put("latitude", latitude != 0 ? latitude : "");//47.6062
        hashMap.put("longitude", longitude != 0 ? longitude : "");//122.3321
        hashMap.put("deviceid", getDeviceId());

        fillerApiWrapper.startDeliveryCall(AppPreferences.getInstance(LoginActivity.this).getSignInResult() != null ?
                        AppPreferences.getInstance(LoginActivity.this).getSignInResult().optString("AuthNToken") : "",
                hashMap, new Callback<InterestDTO>() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(Call<InterestDTO> call, Response<InterestDTO> response) {
                        if (response.isSuccessful()) {
                            InterestDTO responseInterestObj = response.body();
                            if (responseInterestObj != null) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("responseInterest",responseInterestObj);
                                bundle.putString("fulfillerId",hashMap.get("fulfillerid").toString());
                                bundle.putString("FulfillerName",hashMap.get("name").toString());
                                startActivity(new Intent(LoginActivity.this,StartDelivery.class)
                                        .putExtras(bundle));
                                if (startDeliveringLayout.isShown()) {
                                    startDeliveringLayout.startAnimation(animation_slide_down);
                                    startDeliveringLayout.setVisibility(View.GONE);
                                    enableDisableView(loginLayout,true);
                                }
                            }else {
                                AppUtil.toast(LoginActivity.this,"The response is empty");
                            }
                        } else {
                            try {
                                AppUtil.parseErrorMessage(LoginActivity.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<InterestDTO> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(LoginActivity.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });

    }

    private Object getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = telephonyManager.getDeviceId();
        Log.d(LoginActivity.class.getSimpleName(), "Device ID :: " + deviceID);
        return deviceID;
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }
    public void finishActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        Location mCurrentLocation = location;
        if (mCurrentLocation != null) {
            Log.e(TAG, "Latitude :: " + mCurrentLocation.getLatitude() + " Longitute :: " + mCurrentLocation.getLongitude());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LoginActivity.this);
        }
    }
}
