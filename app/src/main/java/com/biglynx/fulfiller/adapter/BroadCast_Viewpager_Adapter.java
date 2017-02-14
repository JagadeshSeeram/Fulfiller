package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.ui.FulfillmentDetails;
import com.biglynx.fulfiller.utils.AppUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class BroadCast_Viewpager_Adapter extends PagerAdapter {

    private Context mContext;
    private List<FulfillersDTO> fullfillerList;
    LayoutInflater inflater;
    BroadCast broadCast;

    public BroadCast_Viewpager_Adapter(Context mContext, BroadCast broadCast, List<FulfillersDTO> fullfillerLists) {

        this.mContext = mContext;
        this.broadCast = broadCast;
        this.fullfillerList = fullfillerLists;
    }

    @Override
    public int getCount() {
        return fullfillerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.broadcast_viewpager_items, container,
                false);

        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //this will log the page number that was click
                Log.i("TAG", "This page was clicked: " + broadCast.RetailerLocationAddress.RetailerLocationId);
                mContext.startActivity(new Intent(mContext, FulfillmentDetails.class)
                        .putExtra("retaileLocId", broadCast.RetailerLocationAddress.RetailerLocationId)
                );
            }
        });

        TextView orders_tv = (TextView) itemView.findViewById(R.id.orders_tv);
        TextView miles_tv = (TextView) itemView.findViewById(R.id.miles_tv);
        TextView approx_time_tv = (TextView) itemView.findViewById(R.id.approx_time_tv);
        TextView price_tv = (TextView) itemView.findViewById(R.id.price_tv);
        TextView price_type_tv = (TextView) itemView.findViewById(R.id.price_type_tv);
        ImageView price_tag_imv = (ImageView) itemView.findViewById(R.id.price_tag_imv);

        if (fullfillerList.get(position).PriceType.toLowerCase().contains("fixed")) {
            price_tag_imv.setImageResource(R.drawable.fixed_price);
            price_type_tv.setText("PAYS");
            price_tv.setText("$" + AppUtil.getTwoDecimals(fullfillerList.get(position).Amount));

        } else {
            price_tag_imv.setImageResource(R.drawable.bidding_price);
            price_type_tv.setText("BID");
            price_tv.setText("NOW");
        }

        orders_tv.setText(fullfillerList.get(position).OrderCount);
        miles_tv.setText(AppUtil.getTwoDecimals(fullfillerList.get(position).TotalDistance));
        approx_time_tv.setText(TimeUnit.SECONDS.toMinutes(Long.parseLong(fullfillerList.get(position).TotalApproxTimeInSeconds)) + " Min");

        ((ViewPager) container).addView(itemView);
        return itemView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
