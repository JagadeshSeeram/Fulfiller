package com.biglynx.fulfiller.adapter;


import android.content.Context;
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
import com.biglynx.fulfiller.models.BroadCast;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class Broadcast_Adapter extends BaseAdapter implements ViewPager.OnPageChangeListener {

    Context Context;
    List<BroadCast> broadCastList;
    LayoutInflater inflater;
    private int dotsCount;
    private ImageView[] dots;

    public Broadcast_Adapter(Context mContext, List<BroadCast> mbroadCastList) {
        Context = mContext;
        this.broadCastList = mbroadCastList;
        inflater = LayoutInflater.from(this.Context);
    }

    @Override
    public int getCount() {
        Log.d("recent search count", "" + broadCastList.size());
        return broadCastList.size();

    }

    @Override
    public Object getItem(int position) {
        return broadCastList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.broadcast_listitems, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.retailer_name_tv.setText(broadCastList.get(position).BusinessLegalName);
        mViewHolder.store_tv.setText(broadCastList.get(position).RetailerLocationAddress.LocationType);
        mViewHolder.warehouse_tv.setText("");
        mViewHolder.address_tv.setText(broadCastList.get(position).RetailerLocationAddress.RetailerLocationAddress.AddressLine1 + "," +
                broadCastList.get(position).RetailerLocationAddress.RetailerLocationAddress.City + "," +
                broadCastList.get(position).RetailerLocationAddress.RetailerLocationAddress.State + "," +
                broadCastList.get(position).RetailerLocationAddress.RetailerLocationAddress.CountryName);
        //mViewHolder.distance_tv.setText(broadCastList.get(position).MaxDistance+" Miles");
        BroadCast_Viewpager_Adapter broadCastViewpagerAdapter = new BroadCast_Viewpager_Adapter(Context, broadCastList.get(position), broadCastList.get(position).Fulfillments);
        mViewHolder.broacast_viewpager.setAdapter(broadCastViewpagerAdapter);
        mViewHolder.circularIndicator.setViewPager(mViewHolder.broacast_viewpager);
//        setUiPageViewController(mViewHolder.pager_indicator, broadCastViewpagerAdapter);

//        mViewHolder.broacast_viewpager.setOnPageChangeListener(Broadcast_Adapter.this);
        return convertView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(Context.getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        // dots[position].setImageDrawable(Context.getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyViewHolder {
        TextView retailer_name_tv, store_tv, warehouse_tv, address_tv, distance_tv;
        ViewPager broacast_viewpager;
        private CircleIndicator circularIndicator;
        //        LinearLayout pager_indicator;

        public MyViewHolder(View item) {

            broacast_viewpager = (ViewPager) item.findViewById(R.id.broacast_viewpager);
            retailer_name_tv = (TextView) item.findViewById(R.id.retailer_name_tv);
            store_tv = (TextView) item.findViewById(R.id.store_tv);
            warehouse_tv = (TextView) item.findViewById(R.id.warehouse_tv);
            address_tv = (TextView) item.findViewById(R.id.address_tv);
            distance_tv = (TextView) item.findViewById(R.id.distance_tv);
            circularIndicator = (CircleIndicator) item.findViewById(R.id.indicator);
        }
    }

    private void setUiPageViewController(LinearLayout pager_indicator, BroadCast_Viewpager_Adapter broadCastViewpagerAdapter) {
        pager_indicator.removeAllViews();
        dotsCount = 0;
        dotsCount = broadCastViewpagerAdapter.getCount();
        Log.d("count", "" + broadCastViewpagerAdapter.getCount());
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(Context);
            dots[i].setImageDrawable(Context.getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(8, 0, 8, 0);
            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(Context.getResources().getDrawable(R.drawable.selecteditem_dot));
    }


}
