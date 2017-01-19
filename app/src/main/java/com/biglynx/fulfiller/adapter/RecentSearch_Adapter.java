package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.RecentSearch;

import java.util.List;


public class RecentSearch_Adapter extends BaseAdapter {
    Context Context;
    List<RecentSearch> recentSearchList;
    LayoutInflater inflater;
    public RecentSearch_Adapter(Context mContext, List<RecentSearch> recentSearchLists){
        Context=mContext;
        recentSearchList=recentSearchLists;
        inflater = LayoutInflater.from(this.Context);
    }

    @Override
    public int getCount() {
        Log.d("recent search count",""+recentSearchList.size());
        return recentSearchList.size();

    }

    @Override
    public Object getItem(int position) {
        return recentSearchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recent_search, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.place_tv.setText(recentSearchList.get(position).place);

        return convertView;
    }

    private class MyViewHolder {
        TextView place_tv;

        public MyViewHolder(View item) {
            place_tv = (TextView) item.findViewById(R.id.place_tv);

        }
    }
}
