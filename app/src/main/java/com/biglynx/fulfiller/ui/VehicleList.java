package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.biglynx.fulfiller.BuildConfig;
import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.CustomerAddrs_Adapter;
import com.biglynx.fulfiller.adapter.FulfillerConfirmAdapter;
import com.biglynx.fulfiller.adapter.FulfillerPendingAdapter;
import com.biglynx.fulfiller.adapter.VehiclesAdapter;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.Vehicles;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.network.HttpAdapter;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/**
 * Copyright (c) 206 BigLynx
 * <p>
 * All Rights reserved.
 */

/*
* @author Ramakrishna on 8/16/2016
* @version 1.0
*
*/

public class VehicleList extends AppCompatActivity implements View.OnClickListener, NetworkOperationListener {
    VehiclesAdapter vehiclesAdapter;
    ImageView icon_back, toolbar_end_imv;
    TextView companyname_tv;
    ListView listView;
    TextView addvechile_tv, no_vehicles_tv;
    FullFillerApiWrapper apiWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customeraddress);

        initViews();
        callService();
    }

    private void callService() {

        if (!Common.isNetworkAvailable(VehicleList.this)) {
            AppUtil.toast(VehicleList.this, getString(R.string.check_interent_connection));
            return;
        }

        Common.showDialog(VehicleList.this);
        apiWrapper.vehicleListCall(AppPreferences.getInstance(VehicleList.this).getSignInResult().optString("AuthNToken"), new Callback<List<Vehicles>>() {
            @Override
            public void onResponse(Call<List<Vehicles>> call, Response<List<Vehicles>> response) {
                if (response.isSuccessful()) {
                    List<Vehicles> vehiclesList = response.body();
                    if (vehiclesList != null && vehiclesList.size() > 0) {
                        if (!listView.isShown())
                            listView.setVisibility(View.VISIBLE);
                        no_vehicles_tv.setVisibility(View.GONE);
                        vehiclesAdapter = new VehiclesAdapter(VehicleList.this, vehiclesList);
                        listView.setAdapter(vehiclesAdapter);
                    }
                } else {
                    try {
                        AppUtil.parseErrorMessage(VehicleList.this, response.errorBody().string());
                    } catch (IOException e) {
                        if (response.code() != 200)
                            AppUtil.toast(VehicleList.this, OOPS_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                    }

                    if (listView.isShown())
                        listView.setVisibility(View.GONE);
                    no_vehicles_tv.setVisibility(View.VISIBLE);
                }
                Common.disMissDialog();
            }

            @Override
            public void onFailure(Call<List<Vehicles>> call, Throwable t) {
                if (listView.isShown())
                    listView.setVisibility(View.GONE);
                no_vehicles_tv.setVisibility(View.VISIBLE);
                if (!(t instanceof EOFException))
                    AppUtil.toast(VehicleList.this, OOPS_SOMETHING_WENT_WRONG);
                Common.disMissDialog();
            }
        });

    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        toolbar_end_imv = (ImageView) findViewById(R.id.toolbar_end_imv);
        addvechile_tv = (TextView) findViewById(R.id.addvechile_tv);

        toolbar_end_imv.setImageResource(R.drawable.ic_add_n);
        icon_back.setVisibility(View.VISIBLE);
        toolbar_end_imv.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        toolbar_end_imv.setOnClickListener(this);
        addvechile_tv.setOnClickListener(this);
        companyname_tv.setText("VEHICLES");
        listView = (ListView) findViewById(R.id.listview);
        no_vehicles_tv = (TextView) findViewById(R.id.no_vehicles_tv);
        apiWrapper = new FullFillerApiWrapper();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.addvechile_tv:
                startActivity(new Intent(this, AddVehicle.class));
                break;
            case R.id.toolbar_end_imv:
                startActivity(new Intent(this, AddVehicle.class));
                break;
        }
    }

    @Override
    public void operationCompleted(NetworkResponse networkResponse) {
        Common.disMissDialog();
        List<Vehicles> vehiclesList = new ArrayList<>();
        if (networkResponse.getStatusCode() == 200) {
            try {
                JSONArray result = new JSONArray(networkResponse.getResponseString());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.optJSONObject(i);
                    Vehicles vehicles = new Gson().fromJson(jsonObject.toString(), Vehicles.class);
                    vehiclesList.add(vehicles);

                }
                vehiclesAdapter = new VehiclesAdapter(this, vehiclesList);
                listView.setAdapter(vehiclesAdapter);
                Common.setListViewHeightBasedOnItems(listView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
