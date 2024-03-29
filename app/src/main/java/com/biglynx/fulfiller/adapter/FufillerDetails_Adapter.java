package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.ui.FulfillmentDetails;

import java.util.List;


public class FufillerDetails_Adapter extends BaseAdapter {
    Context Context;
    List<FulfillersDTO> fulfillersDTOList;
    LayoutInflater inflater;

    public FufillerDetails_Adapter(Context mContext, List<FulfillersDTO> fulfillersDTOLists) {
        Context = mContext;
        fulfillersDTOList = fulfillersDTOLists;
        inflater = LayoutInflater.from(this.Context);
    }

    @Override
    public int getCount() {
        Log.d("recent search count", "" + fulfillersDTOList.size());
        return fulfillersDTOList.size();

    }

    @Override
    public Object getItem(int position) {
        return fulfillersDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recent_search, parent, false);
            convertView.setBackgroundColor(Color.parseColor("#000000"));
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        if (position == 0) {
            mViewHolder.place_tv.setSelected(true);
        }
        mViewHolder.place_tv.setText("FULFILMENT " + (position + 1));
        mViewHolder.place_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FulfillmentDetails fulfillmentDetailsFrag = (FulfillmentDetails) Context;
                fulfillmentDetailsFrag.fillfromData(position, true);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    parent.getChildAt(i).findViewById(R.id.place_tv).setSelected(false);
                }
                v.setSelected(true);
            }
        });

        return convertView;
    }

    private class MyViewHolder {
        TextView place_tv;

        public MyViewHolder(View item) {
            place_tv = (TextView) item.findViewById(R.id.place_tv);
            place_tv.setClickable(true);
            place_tv.setTextColor(Context.getResources().getColorStateList(R.color.list_item_selector));
        }
    }
}
