package com.biglynx.fulfiller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.utils.AppUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class FulfillerPendingAdapter extends BaseAdapter {
    android.content.Context Context;
    List<FulfillersDTO> confirmList;
    LayoutInflater inflater;
    boolean flag;
    SimpleDateFormat simpleDateFormat;

    public FulfillerPendingAdapter(Context mContext, List<FulfillersDTO> mConfirmList, boolean b) {
        Context = mContext;
        flag=b;
        confirmList = mConfirmList;
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

        mViewHolder.price.setText("$" + confirmList.get(position).Amount);
        Log.d("json object",confirmList.get(position).toString());

        if(flag){
            mViewHolder.price.setText("$" + confirmList.get(position).Amount);
            try {
                if(confirmList.get(position).InterestExpirationDateTime==null || confirmList.get(position).InterestExpirationDateTime.equals(null)){

                }else {
                    Date myDate = simpleDateFormat.parse(confirmList.get(position).InterestExpirationDateTime);
                    mViewHolder.due.setText(AppUtil.getLocalDateFormat(confirmList.get(position).InterestExpirationDateTime));
                    Date date = new Date();
                    printDifference(date, myDate, mViewHolder.expired_tv);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            mViewHolder.expired_tv.setVisibility(View.GONE);
            //mViewHolder.price.setText("$" + confirmList.get(position).Amount + "\n" + confirmList.get(position).Status);
            mViewHolder.price.setText(confirmList.get(position).Status);
            if(confirmList.get(position).InterestExpirationDateTime==null ||
                    confirmList.get(position).InterestExpirationDateTime.equals(null)){

            }else {
                mViewHolder.due.setText(AppUtil.getLocalDateFormat(confirmList.get(position).InterestExpirationDateTime));
            }

        }

        return convertView;
    }

    public void printDifference(Date startDate, Date endDate, TextView expired_tv){
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;



        if(String.valueOf(elapsedSeconds).startsWith("-")||String.valueOf(elapsedDays).startsWith("-")||
                String.valueOf(elapsedHours).startsWith("-")||String.valueOf(elapsedMinutes).startsWith("-")){
            expired_tv.setVisibility(View.VISIBLE);
        }else{
            expired_tv.setVisibility(View.GONE);
        }
    }
    private class MyViewHolder {
        TextView retailername, price,due,expired_tv;
        ImageView ivIcon;

        public MyViewHolder(View item) {
            retailername = (TextView) item.findViewById(R.id.retailer_name_tv);
            price = (TextView) item.findViewById(R.id.price_tv);
            due = (TextView) item.findViewById(R.id.due_tv);
            expired_tv= (TextView) item.findViewById(R.id.expired_tv);
        }
    }
}
