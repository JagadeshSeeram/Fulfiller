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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.CustomerAddrs_Adapter;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.BusinessHours;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.OrderAddress;
import com.biglynx.fulfiller.models.Orders;
import com.biglynx.fulfiller.network.HttpAdapter;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Biglynx on 8/8/2016.
 */

public class CustomerAddress extends AppCompatActivity implements
        View.OnClickListener {

    ListView listView;
    int positon;
    ImageView icon_back;
    TextView companyname_tv;
    CustomerAddrs_Adapter customerAddrsAdapter;
    private List<Orders> ordersList;
    private TextView store_name_tv;
    private BusinessHours businessHours = null;
    private LinearLayout timingsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customeraddress);

        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        store_name_tv = (TextView) findViewById(R.id.store_name_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        ordersList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview);
        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        timingsLayout = (LinearLayout) findViewById(R.id.layout_timings);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("ORDERS_LIST")) {
                ordersList = getIntent().getExtras().getParcelableArrayList("ORDERS_LIST");
                timingsLayout.setVisibility(View.GONE);
                companyname_tv.setText("Customer Addresses");
                customerAddrsAdapter = new CustomerAddrs_Adapter(this, ordersList);
                listView.setAdapter(customerAddrsAdapter);
            } else if (getIntent().hasExtra("STORE_HRS")) {
                businessHours = getIntent().getExtras().getParcelable("STORE_HRS");
                timingsLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                companyname_tv.setText("Store hours");
                store_name_tv.setText(AppUtil.ifNotEmpty(businessHours.BusinessHoursFriendlyName) ?
                        businessHours.BusinessHoursFriendlyName : "Timings");
                showTimings();
            }
        }
    }

    private void showTimings() {
        for (int i = 0; i < 7; i++) {
            View view = getLayoutInflater().inflate(R.layout.view_store_timings_tem, null);
            TextView working_hrs_tv = (TextView) view.findViewById(R.id.working_hrs_tv);
            TextView day_tv = (TextView) view.findViewById(R.id.day_tv);

            switch (i) {
                case 0:
                    day_tv.setText("Monday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.MondayStart) ? businessHours.MondayStart : "")
                            + " - " + (AppUtil.ifNotEmpty(businessHours.MondayEnd) ? businessHours.MondayEnd : ""));
                    break;
                case 1:
                    day_tv.setText("Tuesday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.TuesdayStart) ? businessHours.TuesdayStart : "")
                            + " - " + (AppUtil.ifNotEmpty(businessHours.TuesdayEnd) ? businessHours.TuesdayEnd : ""));
                    break;
                case 2:
                    day_tv.setText("Wednesday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.WednesdayStart) ? businessHours.WednesdayStart : "")
                            + " - " + (AppUtil.ifNotEmpty(businessHours.WednesdayEnd) ? businessHours.WednesdayEnd : ""));
                    break;
                case 3:
                    day_tv.setText("Thursday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.ThursdayStart) ? businessHours.ThursdayStart : "")
                            + " - " + (AppUtil.ifNotEmpty(businessHours.ThursdayEnd) ? businessHours.ThursdayEnd : ""));
                    break;
                case 4:
                    day_tv.setText("Friday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.FridayStart) ? businessHours.FridayStart : "")
                            + " - " + (AppUtil.ifNotEmpty(businessHours.FridayEnd) ? businessHours.FridayEnd : ""));
                    break;
                case 5:
                    day_tv.setText("Saturday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.SaturdayStart) ? businessHours.SaturdayStart : "")
                            + "-" + (AppUtil.ifNotEmpty(businessHours.SaturdayEnd) ? businessHours.SaturdayEnd : ""));
                    break;
                case 6:
                    day_tv.setText("Sunday");
                    working_hrs_tv.setText((AppUtil.ifNotEmpty(businessHours.SundayStart) ? businessHours.SundayStart : "")
                            + "-" + (AppUtil.ifNotEmpty(businessHours.SundayEnd) ? businessHours.SundayEnd : ""));
                    break;
            }

            timingsLayout.addView(view);
        }
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
