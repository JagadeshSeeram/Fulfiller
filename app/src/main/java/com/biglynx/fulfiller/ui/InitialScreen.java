package com.biglynx.fulfiller.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.BuildConfig;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.InitialScreenAdapter;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import me.relex.circleindicator.CircleIndicator;


public class InitialScreen extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator, joinfree_LI;
    private int dotsCount;
    private ImageView[] dots;
    private InitialScreenAdapter mAdapter;
    private ImageView show_hide_imv;
    private TextView login_tv, joinfree_tv, condition_click_tv;
    SignInButton googlelogin_tv;
    ;
    LoginButton fb_login_tv;
    private CallbackManager callbackManager;
    GoogleApiClient google_api_client;
    SharedPreferences userDetails_shardP;
    private int[] mImageResources = {
            R.drawable.sc_1,
            R.drawable.sc_2,
            R.drawable.sc_3,
            R.drawable.sc_4,

    };
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;
    private CircleIndicator circularIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // To make activity full screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        is_intent_inprogress = false;

        FacebookSdk.sdkInitialize(this);
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        setContentView(R.layout.viewpager_footer);
        this.userDetails_shardP = getSharedPreferences("Userdetails", 0);
        intro_images = (ViewPager) findViewById(R.id.pager_introduction);
        circularIndicator = (CircleIndicator) findViewById(R.id.indicator);
        //pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        mAdapter = new InitialScreenAdapter(InitialScreen.this, mImageResources);
        login_tv = (TextView) findViewById(R.id.login_tv);
        joinfree_tv = (TextView) findViewById(R.id.joinfree_tv);
        fb_login_tv = (LoginButton) findViewById(R.id.login_button);
        googlelogin_tv = (SignInButton) findViewById(R.id.sign_in_button);
        condition_click_tv = (TextView) findViewById(R.id.condition_click_tv);
        joinfree_LI = (LinearLayout) findViewById(R.id.joinfree_LI);
        show_hide_imv = (ImageView) findViewById(R.id.show_hide_imv);

        googlelogin_tv.setSize(SignInButton.SIZE_STANDARD);
        googlelogin_tv.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});

        fb_login_tv.setReadPermissions(Arrays.asList(
                "user_status", "public_profile", "email", "user_birthday", "user_friends"));

        callbackManager = CallbackManager.Factory.create();
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        Common.setGooglePlusButtonText(googlelogin_tv, "Google+");
        MyApplication.getInstance().printHashKey();
        show_hide_imv.setOnClickListener(this);
        joinfree_tv.setOnClickListener(this);
        fb_login_tv.setOnClickListener(this);
        googlelogin_tv.setOnClickListener(this);
        condition_click_tv.setOnClickListener(this);
        login_tv.setOnClickListener(this);
        joinfree_tv.setOnClickListener(this);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        //setUiPageViewController();
        circularIndicator.setViewPager(intro_images);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.packagename",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (resultCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                //progress_dialog.dismiss();

            }
            is_intent_inprogress = false;
            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        Log.d("is_intent_inprogress", "" + is_intent_inprogress);
    }


    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv:
               /* intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount)
                        ? intro_images.getCurrentItem() + 1 : 0);*/
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.joinfree_tv:

//                AppUtil.SlideUP(joinfree_LI, this);
//                joinfree_LI.setVisibility(View.VISIBLE);
                startActivity(new Intent(InitialScreen.this, SelectRegistration.class));
                break;
            case R.id.show_hide_imv:

                AppUtil.SlideDown(joinfree_LI, this);
                joinfree_LI.setVisibility(View.GONE);
                break;

            case R.id.login_button:
                // Callback registration
                fb_login_tv.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                SharedPreferences.Editor editor = userDetails_shardP.edit();
                                try {
                                    String photo = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                                    editor.putString("fbid", object.getString("id"));
                                    editor.putString("name", object.getString("name"));
                                    editor.putString("email", object.getString("email"));
                                    editor.putString("photo", photo);
                                    editor.commit();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(InitialScreen.this, SelectRegistration.class)
                                );

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                        LoginManager.getInstance().logOut();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("fb", " cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("fb", " error");
                    }
                });
                break;
            case R.id.sign_in_button:
                is_signInBtn_clicked = true;
                gPlusSignIn();
                break;
            case R.id.condition_click_tv:
                startActivity(new Intent(this, ExplinFulfiller.class));
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        /*for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));*/

        /*if (position + 1 == dotsCount) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        Log.d("conntected to gogole+", "connected");
        getProfileInfo();
        // Update the UI after signin
    }

    @Override
    public void onConnectionSuspended(int i) {

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
 /*
     get user's information name, email, profile pic,Date of birth,tag line and about me
     */

    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                SharedPreferences.Editor editor = userDetails_shardP.edit();
                editor.putString("id", currentPerson.getId());
                editor.putString("name", currentPerson.getDisplayName());
                editor.putString("email", Plus.AccountApi.getAccountName(google_api_client));
                editor.putString("photo", currentPerson.getImage().getUrl());
                editor.commit();
                google_api_client.disconnect();
                startActivity(new Intent(this, SelectRegistration.class)
                );
            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sign-in into the Google + account
     */

    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected", "connected");
            //resolveSignInError();
            google_api_client.connect();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("user connection result", connectionResult.hasResolution() + "," + connectionResult);
        if (!connectionResult.hasResolution()) {
            return;
        }

        connection_result = connectionResult;

        if (is_signInBtn_clicked) {

            resolveSignInError();
        }
       /* if (!is_intent_inprogress) {

            connection_result = connectionResult;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }*/
    }
}