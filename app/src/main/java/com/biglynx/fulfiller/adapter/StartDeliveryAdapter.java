package com.biglynx.fulfiller.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.Orders;
import com.biglynx.fulfiller.ui.StartDelivery;
import com.biglynx.fulfiller.utils.AppUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.biglynx.fulfiller.utils.AppUtil.context;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class StartDeliveryAdapter extends RecyclerView.Adapter<StartDeliveryAdapter.ViewHolder> {
    private MySpinnerAdapter myAdapter;
    private PhoneNumberUtil phoneNumberUtil;
    Context mContext;
    List<Orders> ordersList;
    LayoutInflater inflater;
    SimpleDateFormat simpleDateFormat;
    private boolean spinnerSelectionViaCode = false;
    private boolean userSelected = false;


    public StartDeliveryAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(this.mContext);
        ordersList = new ArrayList<>();
        phoneNumberUtil = PhoneNumberUtil.getInstance();
        myAdapter = new MySpinnerAdapter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.startdelivery_ord_items_new, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder mViewHolder, final int position) {
        mViewHolder.name_tv.setText(ordersList.get(position).CustomerFirstName);
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(ordersList.get(position).CustomerPhone, "US");
            String usFormatNUmber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            String finalNumber = usFormatNUmber.split(" ")[1];
            mViewHolder.phoneno_tv.setText(finalNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
            mViewHolder.phoneno_tv.setText(ordersList.get(position).CustomerPhone);
        }
        mViewHolder.address_tv.setText(ordersList.get(position).OrderAddress.AddressLine1 + ", " +
                ordersList.get(position).OrderAddress.City + ", " +
                ordersList.get(position).OrderAddress.State + ", " +
                ordersList.get(position).OrderAddress.CountryName);
        mViewHolder.copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppUtil.copyText(mContext,"Details",mViewHolder.address_tv.getText().toString().trim());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewHolder.openinmap_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = mViewHolder.address_tv.getText().toString().trim();
                String geoUrl = "geo:" + ordersList.get(position).OrderAddress.Latitude + "," +
                        ordersList.get(position).OrderAddress.Longitude + "?";
                Uri gmmIntentUri = Uri.parse(geoUrl + "q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(mContext.getPackageManager()) != null)
                    mContext.startActivity(mapIntent);
            }
        });
        if (ordersList.get(position).DeliveryStatusId == 4) {
            mViewHolder.spinner.setSelection(2);
//            mViewHolder.time_tv.setText("");
            myAdapter.notifyDeliveryID(4);
        } else if (ordersList.get(position).DeliveryStatusId == 5) {
            mViewHolder.spinner.setSelection(1);
            myAdapter.notifyDeliveryID(5);
        } else {
            mViewHolder.spinner.setSelection(0);
  //          mViewHolder.time_tv.setText("");
        }


        mViewHolder.spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelected = true;
                return false;
            }
        });
        mViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int adapterPosition, long id) {
                if (userSelected) {
                    userSelected = false;
                    Log.d("Slected Type ", " :: " + parent.getAdapter().getItem(adapterPosition));
                    StartDelivery startDelivery = (StartDelivery) mContext;
                    if (parent.getAdapter().getItem(adapterPosition).equals("Started"))
                        startDelivery.callUpdateDeliveryStatusAPi(4, ordersList.get(position).OrderId);
                    else if (parent.getAdapter().getItem(adapterPosition).equals("Delivered"))
                        startDelivery.callUpdateDeliverySignLayout(ordersList.get(position).OrderId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public void setItemsList(List<Orders> itemsList) {
        if (ordersList != null)
            ordersList.clear();
        /*List<Orders> dummyList = itemsList;
        Orders orders = dummyList.get(0);
        orders.DeliveryStatusId = 5;
        itemsList.add(1, orders);*/
        ordersList.addAll(itemsList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv, phoneno_tv, address_tv, copy_tv, openinmap_tv, time_tv;
        Spinner spinner;
        RelativeLayout spinnerLayout;

        public ViewHolder(View item) {
            super(item);
            name_tv = (TextView) item.findViewById(R.id.name_tv);
            phoneno_tv = (TextView) item.findViewById(R.id.phoneno_tv);
            address_tv = (TextView) item.findViewById(R.id.address_tv);
            copy_tv = (TextView) item.findViewById(R.id.copy_tv);
    //        time_tv = (TextView) item.findViewById(R.id.time_tv);
            openinmap_tv = (TextView) item.findViewById(R.id.openinmap_tv);
            spinner = (Spinner) item.findViewById(R.id.spinner);
            spinnerLayout = (RelativeLayout) item.findViewById(R.id.layout_spinner_status);
            spinner.setAdapter(myAdapter);
        }
    }

    public class MySpinnerAdapter extends BaseAdapter {
        int deliveryId = -1;
        ArrayList<String> spinnerList = new ArrayList<>();

        MySpinnerAdapter() {
            spinnerList.add("Not Started");
            spinnerList.add("Delivered");
            spinnerList.add("Started");
        }

        public void notifyDeliveryID(int id) {
            deliveryId = id;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return spinnerList.size();
        }

        @Override
        public Object getItem(int i) {
            return spinnerList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = inflater.inflate(R.layout.view_status_spinner, null);
            TextView spinnerText = (TextView) view.findViewById(R.id.tv);
            spinnerText.setText(spinnerList.get(position));

            if (deliveryId != -1 && deliveryId == 4) {
                if (spinnerText.getText().toString().equals("Not Started")) {
                    spinnerText.setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
            } else if (deliveryId != -1 && deliveryId == 5) {
                if (spinnerText.getText().toString().equals("Not Started") ||
                        spinnerText.getText().toString().equals("Started")) {
                    spinnerText.setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
            } else if (deliveryId != -1 || deliveryId == 0) {

            }
            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            if (deliveryId != -1 && deliveryId == 4) {
                if (this.getItem(position).equals("Not Started")) {
                    return false;
                }
            }
            if (deliveryId != -1 && deliveryId == 5) {
                if (this.getItem(position).equals("Not Started") ||
                        this.getItem(position).equals("Started")) {
                    return false;
                }
            }
            return true;
        }
    }
}
