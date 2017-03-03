package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.MainActivity;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.models.UserProfile;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.CircleImageView;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/**
 * Created by Biglynx on 7/20/2016.
 */

public class BusinessRegistration extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    LinearLayout business_LI, individual_LI;
    ImageView icon_back;
    Button register;
    EditText contact_per_tv, email_ev, phoneno_tv;
    private EditText businessEdit;
    private String type;
    private EditText addressEdit;
    private EditText cityEdit;
    private Spinner stateSpinner;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private EditText countryEdit;

    private CallbackManager mCallbackManager;
    private View passwordLayout;

    private String loginProvider;
    private String providerKey;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<String> stateSpinnerList;
    private CircularImageView socialLoginProfilePIc;
    private ImageView socialLoginLogo;
    private LinearLayout socialLoginProfile;
    private LinearLayout socialLoginButtons;
    private TextView socialLoginUserName;
    private TextView socialLoginUserEmail;
    private TextView title_tv, termsAndPolicy_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        initViews();
        initializeFacebookLogin();
        initGooglePlus();

    }

    public void finishActivity() {
        Intent intent = new Intent(BusinessRegistration.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        business_LI = (LinearLayout) findViewById(R.id.business_LI);
        individual_LI = (LinearLayout) findViewById(R.id.individual_LI);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        contact_per_tv = (EditText) findViewById(R.id.contact_per_tv);
        email_ev = (EditText) findViewById(R.id.email_ev);
        businessEdit = (EditText) findViewById(R.id.busniess_tv);
        phoneno_tv = (EditText) findViewById(R.id.phoneno_tv);
        addressEdit = (EditText) findViewById(R.id.adress_tv);
        cityEdit = (EditText) findViewById(R.id.city_tv);
        stateSpinner = (Spinner) findViewById(R.id.spinner_satte);
        passwordEdit = (EditText) findViewById(R.id.password_ev);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordEdit = (EditText) findViewById(R.id.confirm_pass_ev);
        countryEdit = (EditText) findViewById(R.id.country_tv);
        register = (Button) findViewById(R.id.register);
        icon_back.setVisibility(View.VISIBLE);
        View facebookLogin = findViewById(R.id.login_button);
        facebookLogin.setOnClickListener(this);
        View googleLogin = findViewById(R.id.sign_in_button);
        socialLoginProfilePIc = (CircularImageView) findViewById(R.id.iv_profile_pic);
        socialLoginLogo = (ImageView) findViewById(R.id.iv_social_login_logo);
        socialLoginProfile = (LinearLayout) findViewById(R.id.layout_social_login_profile);
        socialLoginButtons = (LinearLayout) findViewById(R.id.layout_social_login_buttons);
        socialLoginUserName = (TextView) findViewById(R.id.tv_social_login_username);
        socialLoginUserEmail = (TextView) findViewById(R.id.tv_social_login_user_email);
        title_tv = (TextView) findViewById(R.id.companyname_tv);
        if (!title_tv.isShown())
            title_tv.setVisibility(View.VISIBLE);
        googleLogin.setOnClickListener(this);
        icon_back.setOnClickListener(this);
        register.setOnClickListener(this);

        stateSpinnerList = new ArrayList<>();
        for (String state : getResources().getStringArray(R.array.statesList)) {
            stateSpinnerList.add(state);
        }

        stateSpinner.setAdapter(getSpinnerAdapter(stateSpinnerList));
        type = getIntent().getStringExtra("type");
        if (type.equals("0")) {
            business_LI.setVisibility(View.VISIBLE);
            title_tv.setText(getString(R.string.organization));
        } else {
            business_LI.setVisibility(View.GONE);
            title_tv.setText(getString(R.string.driver));
        }

    }

    public ArrayAdapter getSpinnerAdapter(ArrayList<String> list) {
        return new ArrayAdapter(getApplicationContext(),
                R.layout.view_state_spinner_item, list);
    }

    private void initGooglePlus() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth2_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void initializeFacebookLogin() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        if (object != null) {
                                            socialLoginProfile.setVisibility(View.VISIBLE);
                                            socialLoginButtons.setVisibility(View.GONE);

                                            loginProvider = Constants.FACEBOOK_LOGIN;
                                            providerKey = object.optString("id");
                                            email_ev.setText(object.optString("email"));
                                            contact_per_tv.setText(object.optString("first_name")+" "+object.optString("last_name"));
                                            passwordLayout.setVisibility(View.GONE);

                                            Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + providerKey + "/picture?type=large")
                                                    .error((int) R.drawable.com_facebook_profile_picture_blank_square).into(socialLoginProfilePIc);
                                            socialLoginUserName.setText("Welcome, " + object.optString("first_name"));
                                            //socialLoginUserEmail.setText(object.optString("email"));
                                            socialLoginLogo.setImageResource(R.drawable.ic_fb_n);
                                            //socialLoginLogo.setShadowColor(R.color.facebook_bg);
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
                        socialLoginProfile.setVisibility(View.GONE);
                        socialLoginButtons.setVisibility(View.VISIBLE);
                        Toast.makeText(BusinessRegistration.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void onFacebookLoginClick() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile,email"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
        }
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            socialLoginProfile.setVisibility(View.VISIBLE);
            socialLoginButtons.setVisibility(View.GONE);
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            loginProvider = Constants.GOOGLE_LOGIN;
            providerKey = acct.getId();
            email_ev.setText(acct.getEmail());
            contact_per_tv.setText(acct.getDisplayName());
            passwordLayout.setVisibility(View.GONE);

            Picasso.with(getApplicationContext()).load(acct.getPhotoUrl())
                    .error((int) R.drawable.com_facebook_profile_picture_blank_square).into(socialLoginProfilePIc);
            socialLoginUserName.setText(acct.getDisplayName());
            //socialLoginUserEmail.setText(acct.getEmail());
            socialLoginLogo.setImageResource(R.drawable.ic_google_login);
            //socialLoginLogo.setShadowColor(R.color.gplus_bg);
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            socialLoginProfile.setVisibility(View.GONE);
            socialLoginButtons.setVisibility(View.VISIBLE);
        }

        clearGoogleAccount();
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

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    private void gPlusSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                onFacebookLoginClick();
                break;
            case R.id.sign_in_button:
                gPlusSignIn();
                break;
            case R.id.icon_back:
                finish();
                break;
            case R.id.register:
                final FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
                if (!TextUtils.isEmpty(type)) {
                    if (type.equals("0")) {
                        if (TextUtils.isEmpty(businessEdit.getText())) {
                            AppUtil.toast(this, "Business name cannot be empty!!");
                            return;
                        }
                    }
                }
                if (TextUtils.isEmpty(contact_per_tv.getText())) {
                    AppUtil.toast(this, "Contact Person name cannot be empty!!");
                    return;
                }
                if (TextUtils.isEmpty(email_ev.getText())) {
                    AppUtil.toast(this, "Email cannot be empty!!");
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_ev.getText().toString()).matches()) {
                    AppUtil.toast(this, "Email is not valid!!");
                    return;
                }
                if (TextUtils.isEmpty(stateSpinner.getSelectedItem().toString())) {
                    AppUtil.toast(this, "State cannot be empty!!");
                    return;
                }
                if (passwordLayout.isShown()) {
                    if (TextUtils.isEmpty(passwordEdit.getText())) {
                        AppUtil.toast(this, "Password cannot be empty!!");
                        return;
                    }
                    if (TextUtils.isEmpty(confirmPasswordEdit.getText())) {
                        AppUtil.toast(this, "Confirm password cannot be empty!!");
                        return;
                    }
                    if (!TextUtils.equals(passwordEdit.getText(), confirmPasswordEdit.getText())) {
                        AppUtil.toast(this, "Passwords should be equal!!");
                        return;
                    }
                }
                if (TextUtils.isEmpty(phoneno_tv.getText())) {
                    AppUtil.toast(this, "Phone cannot be empty!!");
                    return;
                }
                if (TextUtils.isEmpty(addressEdit.getText())) {
                    AppUtil.toast(this, "Address cannot be empty!!");
                    return;
                }
                if (TextUtils.isEmpty(cityEdit.getText())) {
                    AppUtil.toast(this, "City cannot be empty!!");
                    return;
                }

                UserProfile profile = new UserProfile();
                if (!TextUtils.isEmpty(type) && type.equals("0")) {
                    profile.BusinessLegalName = businessEdit.getText().toString();
                }
                profile.FirstName = contact_per_tv.getText().toString();
                profile.Email = email_ev.getText().toString();
                profile.Phone = phoneno_tv.getText().toString();
                profile.AddressLine1 = addressEdit.getText().toString();
                profile.City = cityEdit.getText().toString();
                profile.State = stateSpinner.getSelectedItem().toString();
                if (passwordLayout.isShown()) {
                    profile.Password = passwordEdit.getText().toString();
                }
                if (!TextUtils.isEmpty(loginProvider)) {
                    profile.LoginProvider = loginProvider;
                    profile.ProviderKey = providerKey;
                }
                profile.Country = countryEdit.getText().toString();
                if (!TextUtils.isEmpty(type) && type.equals("0")) {

                    if (!Common.isNetworkAvailable(BusinessRegistration.this)) {
                        AppUtil.toast(BusinessRegistration.this, getString(R.string.check_interent_connection));
                        return;
                    }

                    Common.showDialog(BusinessRegistration.this);
                    apiWrapper.registerDeliveryPartner(profile, new Callback<SignInResult>() {
                        @Override
                        public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                            Common.disMissDialog();
                            if (response.isSuccessful()) {
                                SignInResult signInResult = response.body();
                                if (signInResult != null) {
                                    if(!TextUtils.isEmpty(signInResult.FulfillerId) && signInResult.FulfillerId.trim().equals("0")){
                                        AppUtil.toast(BusinessRegistration.this,"Your action could not be completed because there was a problem communicating with server");
                                        return;
                                    }
                                    /*if (!signInResult.Status.equalsIgnoreCase("active"))
                                        signInResult.showNoticeDialog = true;*/
                                    AppUtil.toast(BusinessRegistration.this,"You have registered successfully");
                                    AppPreferences.getInstance(BusinessRegistration.this).setSignInResult(signInResult);
                                    finishActivity();
                                } else {
                                    AppUtil.toast(BusinessRegistration.this, OOPS_SOMETHING_WENT_WRONG);
                                }
                            } else {
                                try {
                                    AppUtil.parseErrorMessage(BusinessRegistration.this, response.errorBody().string());
                                } catch (IOException e) {
                                    AppUtil.toast(BusinessRegistration.this, OOPS_SOMETHING_WENT_WRONG);
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SignInResult> call, Throwable t) {
                            Common.disMissDialog();
                            AppUtil.toast(BusinessRegistration.this, Constants.OOPS_SOMETHING_WENT_WRONG);
                        }
                    });
                } else {
                    Common.showDialog(BusinessRegistration.this);
                    apiWrapper.registerDriver(profile, new Callback<SignInResult>() {
                        @Override
                        public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                            Common.disMissDialog();
                            if (response.isSuccessful()) {
                                SignInResult signInResult = response.body();
                                if (signInResult != null) {
                                    if(!TextUtils.isEmpty(signInResult.FulfillerId) && signInResult.FulfillerId.trim().equals("0")){
                                        AppUtil.toast(BusinessRegistration.this,"Your action could not be completed because there was a problem communicating with server");
                                        return;
                                    }
                                   /* if (!signInResult.Status.equalsIgnoreCase("active"))
                                        signInResult.showNoticeDialog = true;*/
                                    AppPreferences.getInstance(BusinessRegistration.this).setSignInResult(signInResult);
                                    AppUtil.toast(BusinessRegistration.this,"You have registered successfully");
                                    finishActivity();
                                } else {
                                    AppUtil.toast(BusinessRegistration.this, OOPS_SOMETHING_WENT_WRONG);
                                }
                            } else {
                                try {
                                    AppUtil.parseErrorMessage(BusinessRegistration.this, response.errorBody().string());
                                } catch (IOException e) {
                                    AppUtil.toast(BusinessRegistration.this, OOPS_SOMETHING_WENT_WRONG);
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SignInResult> call, Throwable t) {
                            Common.disMissDialog();
                            AppUtil.toast(BusinessRegistration.this, Constants.OOPS_SOMETHING_WENT_WRONG);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
