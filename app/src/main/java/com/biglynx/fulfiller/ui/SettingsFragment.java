package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.Common;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

/*
 *
 * Created by Biglynx on 7/21/2016.
 *
*/

public class SettingsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView name_tv, logout_Li;
    private CircularImageView user_Imv;
    private LinearLayout vehicles_LI,notification_LI,cservices_LI,payments_LI,terms_LI, policy_LI;
    private ImageView editProfile_iv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView account_status;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
        }
        View v = inflater.inflate(R.layout.settings, container, false);
        initViews(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileInfo(false);
    }

    private void initViews(View v) {
        user_Imv=(CircularImageView) v.findViewById(R.id.user_imv);
        name_tv=(TextView) v.findViewById(R.id.name_tv);
        logout_Li=(TextView)v.findViewById(R.id.logout_LI);
        cservices_LI=(LinearLayout)v.findViewById(R.id.cservices_LI);
        vehicles_LI=(LinearLayout)v.findViewById(R.id.vehicles_LI);
        payments_LI = (LinearLayout) v.findViewById(R.id.payments_LI);
        notification_LI=(LinearLayout)v.findViewById(R.id.notification_LI);
        editProfile_iv = (ImageView) v.findViewById(R.id.editProfile_iv);
        terms_LI = (LinearLayout) v.findViewById(R.id.terms_LI);
        policy_LI = (LinearLayout) v.findViewById(R.id.policy_LI);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_To_Refresh_Layout);
        account_status = (TextView) v.findViewById(R.id.account_status);
        swipeRefreshLayout.setOnRefreshListener(this);

        logout_Li.setOnClickListener(this);
        vehicles_LI.setOnClickListener(this);
        notification_LI.setOnClickListener(this);
        cservices_LI.setOnClickListener(this);
        payments_LI.setOnClickListener(this);
        editProfile_iv.setOnClickListener(this);
        terms_LI.setOnClickListener(this);
        policy_LI.setOnClickListener(this);
    }

    private void updateProfileInfo(boolean showRefresh) {

        if (AppPreferences.getInstance(getActivity()).getSignInResult().optString("Role").equals("DeliveryPartner")) {
            if (!TextUtils.isEmpty(AppPreferences.getInstance(getActivity()).getSignInResult().optString("BusinessLegalName")))
                name_tv.setText(AppPreferences.getInstance(getActivity()).getSignInResult().optString("BusinessLegalName"));
            if (!TextUtils.isEmpty(AppPreferences.getInstance(getContext()).getSignInResult().optString("CompanyLogo")))
                Picasso.with(getActivity()).load(AppPreferences.getInstance(getContext()).getSignInResult().optString("CompanyLogo"))
                        .error((int) R.drawable.ic_no_profile_img).into(this.user_Imv);
        } else {
            if (!TextUtils.isEmpty(AppPreferences.getInstance(getActivity()).getSignInResult().optString("FirstName")))
                name_tv.setText(AppPreferences.getInstance(getActivity()).getSignInResult().optString("FirstName"));
            if (!TextUtils.isEmpty(AppPreferences.getInstance(getContext()).getSignInResult().optString("ProfileImage")))
                Picasso.with(getActivity()).load(AppPreferences.getInstance(getContext()).getSignInResult().optString("ProfileImage"))
                        .error((int) R.drawable.ic_no_profile_img).into(this.user_Imv);
        }
        if (!TextUtils.isEmpty(AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status")))
            account_status.setText("Status: " +getStatus(AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status")));
        else
            account_status.setText("");

        swipeRefreshLayout.setRefreshing(false);
    }

    private String getStatus(String status) {
        if (AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status").equalsIgnoreCase("verificationInProgress")) {
            String userStatus[] = AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status").split("In");
            status = ""+userStatus[0]+" In "+userStatus[1];
            return status;
        }
        return status;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.logout_LI:
                Common.showDialog(getActivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppPreferences.getInstance(getContext()).setSignInResult(null);
                        //AppPreferences.getInstance(getContext()).setAccountInfo(null);
                        startActivity(new Intent(getActivity(), InitialScreen.class));
                        getActivity().finishAffinity();
                    }
                }, 3000);

                break;
            case R.id.vehicles_LI:
                startActivity(new Intent(getActivity(), VehicleList.class));
                break;
            case R.id.notification_LI:
                startActivity(new Intent(getActivity(), NotificatoinProximity.class));
                break;
            case R.id.cservices_LI:
                startActivity(new Intent(getActivity(), CustomerSupport.class));
                break;
            case R.id.payments_LI:
                startActivity(new Intent(getActivity(), PaymentsActivity.class));
                break;
            case R.id.editProfile_iv:
                Intent intent = new Intent(getActivity(),EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.terms_LI:
                startActivity(new Intent(getActivity(),TermsActivity.class).putExtra(TermsActivity.TYPE,TermsActivity.TERMS_OF_SERVICE));
                break;
            case R.id.policy_LI:
                startActivity(new Intent(getActivity(),TermsActivity.class).putExtra(TermsActivity.TYPE,TermsActivity.PRIVACY_POLICY));
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Common.disMissDialog();
    }

    @Override
    public void onRefresh() {
        updateProfileInfo(true);
    }
}
