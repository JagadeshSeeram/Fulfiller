package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/**
 * Created by Biglynx on 17/8/2016.
 */

public class NotificatoinProximity extends AppCompatActivity implements
        View.OnClickListener {

    private ImageView icon_back;
    private TextView companyname_tv;
    private Button submitProximityDistance;
    private EditText enterBidEditText;
    private FullFillerApiWrapper apiWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_prox);
        initViews();
    }

    public void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        submitProximityDistance = (Button) findViewById(R.id.bt_submit_proximity_distance);
        enterBidEditText = (EditText) findViewById(R.id.enter_bid_ev);

        companyname_tv.setText("Notificatoins proximity");
        if (AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("Proximity") != null)
            enterBidEditText.setText(AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("Proximity"));
        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        submitProximityDistance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.bt_submit_proximity_distance:
                if (TextUtils.isEmpty(enterBidEditText.getText().toString()))
                    AppUtil.toast(NotificatoinProximity.this, getString(R.string.distance_cant_be_emptry));
                else
                    callService(enterBidEditText.getText().toString());
                break;
        }
    }

    private void callService(String proximityDistance) {

        if (!Common.isNetworkAvailable(NotificatoinProximity.this)) {
            AppUtil.toast(NotificatoinProximity.this, getString(R.string.check_interent_connection));
            return;
        }

        Common.showDialog(NotificatoinProximity.this);
        HashMap<String, Object> infoObject;
        infoObject = getProfileInfo(proximityDistance);
        String editProfileType = null;
        if (AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult() != null) {
            Log.e(NotificatoinProximity.class.getSimpleName(), "AuthToken :: " + AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("AuthNToken"));
            if (AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("Role").equals("DeliveryPartner")) {
                editProfileType = "editprofilepartner";
            } else {
                editProfileType = "editprofileperson";
            }
        }
        apiWrapper = new FullFillerApiWrapper();
        apiWrapper.editProfileModelCall(AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("AuthNToken"),
                editProfileType, infoObject, new Callback<SignInResult>() {
                    @Override
                    public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            SignInResult editProfileModel = response.body();
                            if (editProfileModel != null)
                                AppPreferences.getInstance(NotificatoinProximity.this).setSignInResult(editProfileModel);
                            finish();
                        } else {
                            try {
                                AppUtil.parseErrorMessage(NotificatoinProximity.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(NotificatoinProximity.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }

                            AppUtil.CheckErrorCode(NotificatoinProximity.this, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInResult> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(NotificatoinProximity.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    public HashMap getProfileInfo(String proximityDistance) {
        HashMap<String, Object> editProfileBodyObject = new HashMap<>();
        if (AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult() != null) {
            editProfileBodyObject.put("fulfillerid", AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("FulfillerId"));
            editProfileBodyObject.put("fulfillertype", AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optString("Role"));
            editProfileBodyObject.put("Proximity", proximityDistance);
            editProfileBodyObject.put("ReadyFulfill",
                    AppPreferences.getInstance(NotificatoinProximity.this).getSignInResult().optBoolean("ReadyFufill") ? 1 : 0);
        }

        return editProfileBodyObject;
    }
}
