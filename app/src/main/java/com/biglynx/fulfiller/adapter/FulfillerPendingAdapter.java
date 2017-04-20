package com.biglynx.fulfiller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.listeners.OnRecyclerItemClickListener;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.ui.HomeFragment;
import com.biglynx.fulfiller.ui.InterestDetails;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class FulfillerPendingAdapter extends RecyclerView.Adapter<FulfillerPendingAdapter.ViewHolder> {
    Context mContext;
    List<FulfillersDTO> confirmList = new ArrayList<>();
    LayoutInflater inflater;
    boolean flag;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private OnRecyclerItemClickListener listener;

    public FulfillerPendingAdapter(Context context, List<FulfillersDTO> mConfirmList, boolean b,
                                   OnRecyclerItemClickListener clickListener) {
        mContext = context;
        flag = b;
        confirmList = mConfirmList;
        inflater = LayoutInflater.from(mContext);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        listener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.list_waiting_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder mViewHolder, final int position) {
        mViewHolder.retailername.setText(confirmList.get(position).RetailerName);

        mViewHolder.price.setText("$" + confirmList.get(position).Amount);
        Log.d("json object", confirmList.get(position).toString());

        if (flag) {
            mViewHolder.price.setText("$" + confirmList.get(position).Amount);
            try {
                if (confirmList.get(position).InterestExpirationDateTime == null || confirmList.get(position).InterestExpirationDateTime.equals(null)) {

                } else {
                    Date myDate = simpleDateFormat.parse(confirmList.get(position).InterestExpirationDateTime);
                    mViewHolder.due.setText(AppUtil.getLocalDateFormat(confirmList.get(position).InterestExpirationDateTime));
                    Date date = new Date();
                    printDifference(date, myDate, mViewHolder.expired_tv);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mViewHolder.expired_tv.setVisibility(View.GONE);
            //mViewHolder.price.setText("$" + confirmList.get(position).Amount + "\n" + confirmList.get(position).Status);
            mViewHolder.price.setText(confirmList.get(position).Status);
            if (confirmList.get(position).InterestExpirationDateTime == null ||
                    confirmList.get(position).InterestExpirationDateTime.equals(null)) {

            } else {
                mViewHolder.due.setText(AppUtil.getLocalDateFormat(confirmList.get(position).InterestExpirationDateTime));
            }

        }

        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    if (listener != null)
                        listener.onRecyclerItemClcik(Constants.PENDING, position);
                }else {
                    if (listener != null)
                        listener.onRecyclerItemClcik(Constants.CONFIRM, position);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return confirmList.size();
    }

    public void setList(List<FulfillersDTO> list) {
        if (list == null)
            return;
        if (confirmList != null && confirmList.size() > 0)
            confirmList = new ArrayList<>();
        confirmList.addAll(list);
        Collections.sort(confirmList, new Comparator<FulfillersDTO>() {
            @Override
            public int compare(FulfillersDTO result1, FulfillersDTO result2) {
                try{
                    Date date1 = simpleDateFormat.parse(result1.InterestDateTime);
                    Date date2 = simpleDateFormat.parse(result2.InterestDateTime);
                    return date1.compareTo(date2);
                }catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        notifyDataSetChanged();
    }

    public void printDifference(Date startDate, Date endDate, TextView expired_tv) {
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


        if (String.valueOf(elapsedSeconds).startsWith("-") || String.valueOf(elapsedDays).startsWith("-") ||
                String.valueOf(elapsedHours).startsWith("-") || String.valueOf(elapsedMinutes).startsWith("-")) {
            expired_tv.setVisibility(View.VISIBLE);
        } else {
            expired_tv.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView retailername, price, due, expired_tv;
        ImageView ivIcon;

        public ViewHolder(View item) {
            super(item);
            retailername = (TextView) item.findViewById(R.id.retailer_name_tv);
            price = (TextView) item.findViewById(R.id.price_tv);
            due = (TextView) item.findViewById(R.id.due_tv);
            expired_tv = (TextView) item.findViewById(R.id.expired_tv);
        }
    }
}
