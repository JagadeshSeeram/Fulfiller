package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.Orders;
import com.biglynx.fulfiller.models.RecentSearch;

import java.util.ArrayList;
import java.util.List;


public class CustomerAddrs_Adapter extends BaseAdapter {
    Context Context;
    List<Orders> ordersList = new ArrayList<>();
    LayoutInflater inflater;

    public CustomerAddrs_Adapter(Context mContext, List<Orders> ordersLists) {
        Context = mContext;
        ordersList = ordersLists;
        inflater = LayoutInflater.from(this.Context);
    }

    @Override
    public int getCount() {
        Log.d("no of adderesses", " " + String.valueOf(ordersList.size()));
        return ordersList.size();

    }

    @Override
    public Object getItem(int position) {
        return ordersList != null ? ordersList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customeaddress_items, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.place_tv.setText(ordersList.get(position).OrderAddress.AddressLine1 + ", " +
                ordersList.get(position).OrderAddress.City + ", " +
                ordersList.get(position).OrderAddress.State + ", " +
                ordersList.get(position).OrderAddress.CountryName);

        return convertView;
    }

    private class MyViewHolder {
        TextView place_tv;

        public MyViewHolder(View item) {
            place_tv = (TextView) item.findViewById(R.id.place_tv);

        }
    }
}
