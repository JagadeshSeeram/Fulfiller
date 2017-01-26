package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.CustomerAddrs_Adapter;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.OrderAddress;
import com.biglynx.fulfiller.models.Orders;
import com.biglynx.fulfiller.network.HttpAdapter;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Biglynx on 8/8/2016.
 */

public class CustomerAddress extends AppCompatActivity implements
        View.OnClickListener {

    ListView listView;
    BroadCast broadCast;
    int positon;
    ImageView icon_back;
    TextView companyname_tv;
    CustomerAddrs_Adapter customerAddrsAdapter;
    InterestDTO interestDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customeraddress);

        companyname_tv=(TextView)findViewById(R.id.companyname_tv);
        icon_back=(ImageView) findViewById(R.id.icon_back);

        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        companyname_tv.setText("Customer Addresses");

        if (getIntent().getExtras() != null){
            if (getIntent().hasExtra("broadCast"))
                broadCast=(BroadCast)getIntent().getSerializableExtra("broadCast");
            else
                interestDTO = (InterestDTO) getIntent().getParcelableExtra("interest");

            positon=getIntent().getIntExtra("position",1);
        }


        listView=(ListView)findViewById(R.id.listview);

        customerAddrsAdapter=new CustomerAddrs_Adapter(this,broadCast != null ? broadCast.Fulfillments.get(positon).Orders : null,interestDTO);
        listView.setAdapter(customerAddrsAdapter);

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
