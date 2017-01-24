package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.SupportCategoryModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;


public class CustomerSupport extends AppCompatActivity implements View.OnClickListener {

    private ImageView icon_back;
    private TextView companyname_tv;
    private TextView createTicket_tv, viewTicket_tv;
    private FullFillerApiWrapper apiWrapper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customersupport);
        initViews();
    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        icon_back.setVisibility(View.VISIBLE);
        companyname_tv.setText("Customer Support");
        createTicket_tv = (TextView) findViewById(R.id.create_ticket_tv);
        viewTicket_tv = (TextView) findViewById(R.id.view_ticket_tv);
        apiWrapper = new FullFillerApiWrapper();

        createTicket_tv.setOnClickListener(this);
        viewTicket_tv.setOnClickListener(this);
        icon_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.create_ticket_tv:
                startActivity(new Intent(CustomerSupport.this, CreateTicketActivity.class));
                break;
            case R.id.view_ticket_tv:
                requestAllServiceRequests();
                break;
        }
    }

    private void requestAllServiceRequests() {
        if (!Common.isNetworkAvailable(CustomerSupport.this)){
            AppUtil.toast(CustomerSupport.this,"Network disconnected, Please check...");
            return;
        }
        Common.showDialog(this);
        apiWrapper.getAllTicketsCall(AppPreferences.getInstance(this).getSignInResult() != null ?
                        AppPreferences.getInstance(this).getSignInResult().optString("AuthNToken") : "",
                "BIGLYNX_DELIVERY_PARTNER", new Callback<ArrayList<SupportCategoryModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<SupportCategoryModel>> call, Response<ArrayList<SupportCategoryModel>> response) {
                        if (response.isSuccessful()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("categoriesList", response.body());

                            startActivity(new Intent(CustomerSupport.this, ViewServiceRequestsActivity.class)
                                    .putExtras(bundle));
                        }else {
                            try {
                                AppUtil.parseErrorMessage(CustomerSupport.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(CustomerSupport.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            AppUtil.CheckErrorCode(CustomerSupport.this, response.code());
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<SupportCategoryModel>> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(CustomerSupport.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }
}
