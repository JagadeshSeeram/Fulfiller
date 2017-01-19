package com.biglynx.fulfiller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.utils.AppUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/*
 *
 * Created by Biglynx on 7/22/2016.
 *
 */

public class FulfillerConfirmAdapter extends BaseAdapter{
    Context Context;
    List<FulfillersDTO> confirmList;
    LayoutInflater inflater;
    SimpleDateFormat simpleDateFormat;
    public FulfillerConfirmAdapter(Context mContext, List<FulfillersDTO> mConfirmList){
        Context=mContext;
        confirmList=mConfirmList;
        inflater = LayoutInflater.from(this.Context);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public int getCount() {
        return confirmList.size();
    }

    @Override
    public Object getItem(int position) {
        return confirmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_waiting_items, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.retailername.setText(confirmList.get(position).RetailerName);
        mViewHolder.price.setText("$"+confirmList.get(position).Amount);

        mViewHolder.due.setText(AppUtil.getLocalDateFormat(confirmList.get(position).InterestExpirationDateTime));

        return convertView;
    }

    private class MyViewHolder {
        TextView retailername, price,due,expired_tv;
        ImageView ivIcon;

        public MyViewHolder(View item) {
            retailername = (TextView) item.findViewById(R.id.retailer_name_tv);
            price = (TextView) item.findViewById(R.id.price_tv);
            due = (TextView) item.findViewById(R.id.due_tv);
            expired_tv= (TextView) item.findViewById(R.id.expired_tv);
            expired_tv.setVisibility(View.GONE);
        }
    }
}
