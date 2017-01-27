package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

public class SettingsFragment extends Fragment implements NetworkOperationListener,View.OnClickListener {

    private TextView name_tv, logout_Li;
    private CircularImageView user_Imv;
    private LinearLayout vehicles_LI,notification_LI,cservices_LI,payments_LI,terms_LI, policy_LI;
    private ImageView editProfile_iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);
        initViews(v);

        return v;
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

        if (!TextUtils.isEmpty(AppPreferences.getInstance(getContext()).getSignInResult().optString("CompanyLogo")))
            Picasso.with(getActivity()).load(AppPreferences.getInstance(getContext()).getSignInResult().optString("CompanyLogo"))
                    .error((int) R.drawable.com_facebook_profile_picture_blank_square).into(this.user_Imv);

        if (!TextUtils.isEmpty(AppPreferences.getInstance(getContext()).getSignInResult().optString("FirstName")))
            name_tv.setText(AppPreferences.getInstance(getContext()).getSignInResult().optString("FirstName"));

        logout_Li.setOnClickListener(this);
        vehicles_LI.setOnClickListener(this);
        notification_LI.setOnClickListener(this);
        cservices_LI.setOnClickListener(this);
        payments_LI.setOnClickListener(this);
        editProfile_iv.setOnClickListener(this);
        terms_LI.setOnClickListener(this);
        policy_LI.setOnClickListener(this);
    }

    @Override
    public void operationCompleted(NetworkResponse networkResponse) {
        Common.disMissDialog();

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.logout_LI:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Common.showDialog(getActivity());
                        AppPreferences.getInstance(getContext()).setSignInResult(null);
                        AppPreferences.getInstance(getContext()).setAccountInfo(null);
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
}
