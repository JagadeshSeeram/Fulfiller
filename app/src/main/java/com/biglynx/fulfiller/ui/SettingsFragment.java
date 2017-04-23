package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.biglynx.fulfiller.models.RatingsModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/*
 *
 * Created by Biglynx on 7/21/2016.
 *
*/

public class SettingsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView name_tv, logout_Li;
    private CircularImageView user_Imv;
    private LinearLayout vehicles_LI, notification_LI, cservices_LI, payments_LI, terms_LI, policy_LI, reviews_LI;
    private ImageView editProfile_iv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView account_status;
    private FullFillerApiWrapper apiWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        }
        View v = inflater.inflate(R.layout.settings, container, false);
        initViews(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileInfo();
    }

    private void initViews(View v) {
        apiWrapper = new FullFillerApiWrapper();

        user_Imv = (CircularImageView) v.findViewById(R.id.user_imv);
        name_tv = (TextView) v.findViewById(R.id.name_tv);
        logout_Li = (TextView) v.findViewById(R.id.logout_LI);
        cservices_LI = (LinearLayout) v.findViewById(R.id.cservices_LI);
        vehicles_LI = (LinearLayout) v.findViewById(R.id.vehicles_LI);
        payments_LI = (LinearLayout) v.findViewById(R.id.payments_LI);
        notification_LI = (LinearLayout) v.findViewById(R.id.notification_LI);
        reviews_LI = (LinearLayout) v.findViewById(R.id.rating_LI);

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
        reviews_LI.setOnClickListener(this);
    }

    private void updateProfileInfo() {

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
            account_status.setText("Status: " + getStatus(AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status")));
        else
            account_status.setText("");
    }

    private String getStatus(String status) {
        if (AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status").equalsIgnoreCase("verificationInProgress")) {
            String userStatus[] = AppPreferences.getInstance(getActivity()).getSignInResult().optString("Status").split("In");
            status = "" + userStatus[0] + " In " + userStatus[1];
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
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.terms_LI:
                startActivity(new Intent(getActivity(), TermsActivity.class).putExtra(TermsActivity.TYPE, TermsActivity.TERMS_OF_SERVICE));
                break;
            case R.id.policy_LI:
                startActivity(new Intent(getActivity(), TermsActivity.class).putExtra(TermsActivity.TYPE, TermsActivity.PRIVACY_POLICY));
                break;
            case R.id.rating_LI:
                callRatingsService(true);
                break;
        }
    }

    private void callRatingsService(final boolean showProgress) {
        if (!Common.isNetworkAvailable(getActivity())) {
            AppUtil.toast(getActivity(), "Network disconnected, Please check...");
            return;
        }

        if (showProgress)
            Common.showDialog(getActivity());

        apiWrapper.getReviews(AppPreferences.getInstance(getActivity()).getSignInResult() != null ?
                        AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken") : "",
                new Callback<List<RatingsModel>>() {
                    @Override
                    public void onResponse(Call<List<RatingsModel>> call, Response<List<RatingsModel>> response) {
                        if (response.isSuccessful()) {
                            List<RatingsModel> ratingsList = response.body();
                            if (ratingsList != null)
                                startActivity(new Intent(getActivity(), RatingsActivity.class)
                                        .putParcelableArrayListExtra("ratingsList", (ArrayList<? extends Parcelable>) ratingsList));
                        } else {
                            try {
                                AppUtil.parseErrorMessage(getActivity(), response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(getActivity(), OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            AppUtil.CheckErrorCode(getActivity(), response.code());
                        }

                        if (showProgress)
                            Common.disMissDialog();
                        else
                            swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<List<RatingsModel>> call, Throwable t) {
                        AppUtil.toast(getActivity(), "Unable to show Reviews. Please try later...");
                        if (showProgress)
                            Common.disMissDialog();
                        else
                            swipeRefreshLayout.setRefreshing(false);
                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Common.disMissDialog();
    }

    @Override
    public void onRefresh() {
        updateProfileInfo();
        callRatingsService(false);
    }
}
