package com.biglynx.fulfiller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.AccountConfigModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AlertDialogManager;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

public class AccountConfigFragment extends Fragment implements View.OnClickListener {
    private static final int ACCOUNT_TYPES = 1;
    private EditText bankName_ev, accountType_ev, accountNumber_ev, routingNumber_ev, bankAccountName_ev;
    private ImageView accountTypeArrowImage;
    private TextView submit_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_configuration_view, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        bankName_ev = (EditText) view.findViewById(R.id.bank_name_ev);
        accountType_ev = (EditText) view.findViewById(R.id.account_type_ev);
        accountNumber_ev = (EditText) view.findViewById(R.id.account_number_ev);
        routingNumber_ev = (EditText) view.findViewById(R.id.routing_number_ev);
        bankAccountName_ev = (EditText) view.findViewById(R.id.bank_account_name_ev);
        accountTypeArrowImage = (ImageView) view.findViewById(R.id.account_type_arrow);
        submit_tv = (TextView) view.findViewById(R.id.submit_tv);

        if (AppPreferences.getInstance(getActivity()).getAccountInfo() != null){
            JSONObject jsonObject = AppPreferences.getInstance(getActivity()).getAccountInfo();
            bankName_ev.setText(jsonObject.optString("BankName"));
            accountType_ev.setText(jsonObject.optString("AccountType"));
            accountNumber_ev.setText(jsonObject.optString("AccountNumber"));
            routingNumber_ev.setText(jsonObject.optString("RoutingNumber"));
            bankAccountName_ev.setText(jsonObject.optString("AccountName"));

        }

        accountType_ev.setOnClickListener(this);
        accountTypeArrowImage.setOnClickListener(this);
        submit_tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_type_ev:
            case R.id.account_type_arrow:
                startActivityForResult(new Intent(getActivity(), AccountTypes.class), ACCOUNT_TYPES);
                break;
            case R.id.submit_tv:
                if (TextUtils.isEmpty(bankName_ev.getText().toString().trim()) ||
                        TextUtils.isEmpty(accountType_ev.getText().toString().trim()) ||
                        TextUtils.isEmpty(accountNumber_ev.getText().toString().trim()) ||
                        TextUtils.isEmpty(routingNumber_ev.getText().toString().trim()) ||
                        TextUtils.isEmpty(bankAccountName_ev.getText().toString().trim())) {

                    AlertDialogManager.showAlertOnly(getActivity(), "Account Config", "All Fields are mandatory", "Ok");

                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    try {
                        hashMap.put("Fulfillerid", AppPreferences.getInstance(getActivity()).getSignInResult().getString("FulfillerId"));
                    } catch (JSONException e) {
                        hashMap.put("Fulfillerid", "");
                        e.printStackTrace();
                    }
                    hashMap.put("BankName", bankName_ev.getText().toString());
                    hashMap.put("AccountType", accountType_ev.getText().toString());
                    hashMap.put("AccountNumber", accountNumber_ev.getText().toString());
                    hashMap.put("RoutingNumber", routingNumber_ev.getText().toString());
                    hashMap.put("AccountName", bankAccountName_ev.getText().toString());
                    hashMap.put("Status","Pending");
                    callService(hashMap);
                }
        }
    }

    private void callService(final HashMap<String,String> hashMap) {

        if (!Common.isNetworkAvailable(getActivity())) {
            AppUtil.toast(getActivity(), getString(R.string.check_interent_connection));
            return;
        }
        Common.showDialog(getActivity());
        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        apiWrapper.accountConfigurationCall(AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken"),
                hashMap, new Callback<AccountConfigModel>() {
                    @Override
                    public void onResponse(Call<AccountConfigModel> call, Response<AccountConfigModel> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()){
                            AppPreferences.getInstance(getActivity()).setAccountInfo(hashMap);
                            getActivity().finish();
                        }else {
                            try {
                                AppUtil.parseErrorMessage(getActivity(), response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(getActivity(), OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountConfigModel> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(getActivity(), OOPS_SOMETHING_WENT_WRONG);
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCOUNT_TYPES) {
            if (resultCode == Activity.RESULT_OK) {
                accountType_ev.setText(data != null ? data.getStringExtra("result") : " ");
            }
        }
    }
}
