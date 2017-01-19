package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.RecentSearch;
import com.biglynx.fulfiller.models.SpinnerOrders;

import java.util.List;


public class OrderSpinner extends BaseAdapter {
    Context Context;
    List<SpinnerOrders> spinnerOrdersList;
    LayoutInflater inflater;
    int positionofView;
    public OrderSpinner(Context mContext, List<SpinnerOrders> mSpinnerresult, int position){
        Context=mContext;
        spinnerOrdersList=mSpinnerresult;
        positionofView=position;
        inflater = LayoutInflater.from(this.Context);
    }

    @Override
    public int getCount() {
        Log.d("recent search count",positionofView+","+spinnerOrdersList.size());
        return spinnerOrdersList.size();

    }

    @Override
    public Object getItem(int position) {
        return spinnerOrdersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.orderspinner, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.place_tv.setText(spinnerOrdersList.get(position).name);
        mViewHolder.pos_tv.setText(""+positionofView);

        return convertView;
    }

    private class MyViewHolder {
        TextView place_tv,pos_tv;

        public MyViewHolder(View item) {
            place_tv = (TextView) item.findViewById(R.id.name_tv);
            pos_tv = (TextView) item.findViewById(R.id.pos_tv);

        }
    }
}
