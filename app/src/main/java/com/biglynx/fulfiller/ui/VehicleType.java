package com.biglynx.fulfiller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.VehiclesAdapter;
import com.biglynx.fulfiller.models.Vehicles;
import com.biglynx.fulfiller.network.HttpAdapter;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.Common;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 206 BigLynx
 * <p/>
 * All Rights reserved.
 */

/*
* @author Ramakrishna on 8/16/2016
* @version 1.0
*
*/

public class VehicleType extends AppCompatActivity implements View.OnClickListener {
    VehiclesAdapter vehiclesAdapter;
    ImageView icon_back, toolbar_end_imv;
    TextView companyname_tv;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customeraddress);
        initViews();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                String value = (String) adapter.getItemAtPosition(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", value);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);

        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        companyname_tv.setText("VEHICLE TYPE");
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.mytextview);

        myAdapter.add("Car");
        myAdapter.add("SUV");
        myAdapter.add("Pickup Van");
        myAdapter.add("Commercial cargo van");
        myAdapter.add("commercial heavy truck");
        myAdapter.add("Other");


        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(myAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
        }
    }


}
