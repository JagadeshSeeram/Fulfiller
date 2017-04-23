package com.biglynx.fulfiller.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.MessagesModel;
import com.biglynx.fulfiller.utils.AppUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by snehitha.chinnapally on 1/4/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    List<MessagesModel> modelArrayList;
    LayoutInflater inflater;
    Activity mActivity;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");

    public MessagesAdapter(Activity activity) {
        this.modelArrayList = new ArrayList<>();
        mActivity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView messagesFrom_tv, messages_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            messagesFrom_tv = (TextView) itemView.findViewById(R.id.messageFrom_tv);
            messages_tv = (TextView) itemView.findViewById(R.id.message_tv);
        }
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_message_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {

        if (AppUtil.ifNotEmpty(modelArrayList.get(position).FlulfillerName)) {
            holder.messagesFrom_tv.setTextColor(mActivity.getResources().getColor(R.color.cancel));
            holder.messages_tv.setTextColor(mActivity.getResources().getColor(R.color.cancel));
        } else {
            holder.messagesFrom_tv.setTextColor(mActivity.getResources().getColor(R.color.grey_shade_1));
            holder.messages_tv.setTextColor(mActivity.getResources().getColor(R.color.grey_shade_1));
        }

        holder.messagesFrom_tv.setText((AppUtil.ifNotEmpty(modelArrayList.get(position).FlulfillerName) ?
                modelArrayList.get(position).FlulfillerName : modelArrayList.get(position).RetailerName) + ": ");
        holder.messages_tv.setText(modelArrayList.get(position).Message);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public void setItems(List<MessagesModel> list) {
        if (list == null || list.size() == 0)
            return;
        if (modelArrayList != null) {
            modelArrayList = new ArrayList<>();

            modelArrayList.addAll(list);
            Collections.sort(modelArrayList, new Comparator<MessagesModel>() {
                @Override
                public int compare(MessagesModel o1, MessagesModel o2) {
                    try {
                        Date result1 = simpleDateFormat.parse(o1.DateCreated);
                        Date result2 = simpleDateFormat.parse(o2.DateCreated);
                        return result1.compareTo(result2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }

                }
            });
            notifyDataSetChanged();
        }
    }

    public List<MessagesModel> getModelArrayList() {
        return modelArrayList;
    }
}
