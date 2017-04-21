package com.biglynx.fulfiller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.listeners.OnRecyclerItemClickListener;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/*
 *
 * Created by Biglynx on 7/22/2016.
 *
 */

public class FulfillerConfirmAdapter extends RecyclerView.Adapter<FulfillerConfirmAdapter.ViewHolder> {
    Context Context;
    List<FulfillersDTO> confirmList = new ArrayList<>();
    LayoutInflater inflater;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private OnRecyclerItemClickListener listener;

    public FulfillerConfirmAdapter(Context mContext, List<FulfillersDTO> mConfirmList,
                                   OnRecyclerItemClickListener clickListener) {
        Context = mContext;
        confirmList = mConfirmList;
        inflater = LayoutInflater.from(this.Context);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        listener = clickListener;
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
                    return date2.compareTo(date1);
                }catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        notifyDataSetChanged();
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

        mViewHolder.due.setText(AppUtil.getLocalDateFormat(confirmList.get(position).InterestExpirationDateTime));

        mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onRecyclerItemClcik(Constants.CONFIRM, position);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView retailername, price, due, expired_tv;
        ImageView ivIcon;

        public ViewHolder(View item) {
            super(item);
            retailername = (TextView) item.findViewById(R.id.retailer_name_tv);
            price = (TextView) item.findViewById(R.id.price_tv);
            due = (TextView) item.findViewById(R.id.due_tv);
            expired_tv = (TextView) item.findViewById(R.id.expired_tv);
            expired_tv.setVisibility(View.GONE);
        }
    }
}
