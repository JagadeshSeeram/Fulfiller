package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.ui.FulfillmentDetails;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;


public class BroadCast_Ful_Adapter extends PagerAdapter {

    private Context mContext;
    private List<FulfillersDTO>  fullfillerList;
    LayoutInflater inflater;
    BroadCast broadCast;
    public BroadCast_Ful_Adapter(Context mContext, BroadCast broadCast, List<FulfillersDTO> fullfillerLists) {

        this.mContext = mContext;
        this.broadCast=broadCast;
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
        View itemView = inflater.inflate(R.layout.broadcast_ful_items, container,
                false);

        itemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
                Log.i("TAG", "This page was clicked: " + broadCast.RetailerLocationAddress.RetailerLocationId);
                /*mContext.startActivity(new Intent(mContext, FulfillmentDetails.class)
                .putExtra("broadCast",broadCast)
                .putExtra("position",position)
                        .putExtra("retaileLocId",broadCast.RetailerLocationAddress.RetailerLocationId)
                );*/
                callService(!TextUtils.isEmpty(broadCast.RetailerLocationAddress.RetailerLocationId) ?
                        broadCast.RetailerLocationAddress.RetailerLocationId : "" );
            }
        });
        TextView fullfillments_tv = (TextView) itemView.findViewById(R.id.fullfillments_tv);
        TextView orders_tv = (TextView) itemView.findViewById(R.id.orders_tv);
        TextView miles_tv = (TextView) itemView.findViewById(R.id.miles_tv);
        TextView approx_time_tv = (TextView) itemView.findViewById(R.id.approx_time_tv);
        TextView price_tv = (TextView) itemView.findViewById(R.id.price_tv);
        TextView price_type_tv = (TextView) itemView.findViewById(R.id.price_type_tv);

        ImageView price_tag_imv = (ImageView) itemView.findViewById(R.id.price_tag_imv);

            if(fullfillerList.get(position).PriceType.toLowerCase().contains("fixed")){
                price_tag_imv.setImageResource(R.drawable.ic_fixed_price_n);
                price_tv.setText("$"+fullfillerList.get(position).Amount);
            }else{
                price_tag_imv.setImageResource(R.drawable.ic_bidding_n);
                price_type_tv.setText("Bid now");
            }
        fullfillments_tv.setText("Fulfillments "+(position+1)+"/"+fullfillerList.size());
        orders_tv.setText(fullfillerList.get(position).OrderCount);
        miles_tv.setText(AppUtil.getTwoDecimals(fullfillerList.get(position).TotalDistance));
        approx_time_tv.setText(TimeUnit.SECONDS.toMinutes(Long.parseLong(fullfillerList.get(position).TotalApproxTimeInSeconds))+" Min");


        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    private void callService(String retaileLocId) {
        if (TextUtils.isEmpty(retaileLocId)) {
            AppUtil.toast(mContext, "There is something wrong in fetching the data.Please try again later...");
            return;
        }
        Log.e(FulfillmentDetails.class.getSimpleName(), "retaileLocId :: " + retaileLocId);

        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        if (Common.isNetworkAvailable(MyApplication.getInstance())) {
            Common.showDialog(mContext);
            apiWrapper.broadCastDetailsCall(AppPreferences.getInstance(mContext).getSignInResult() != null ?
                            AppPreferences.getInstance(mContext).getSignInResult().optString("AuthNToken") : "",
                    retaileLocId, new Callback<ArrayList<BroadCast>>() {
                        @Override
                        public void onResponse(Call<ArrayList<BroadCast>> call, Response<ArrayList<BroadCast>> response) {
                            Common.disMissDialog();
                            if (response.isSuccessful()) {
                                List<BroadCast> resultBroadCast = response.body();
                                if (resultBroadCast != null && resultBroadCast.size() > 0) {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("broadcast",resultBroadCast.get(0));
                                    mContext.startActivity(new Intent(mContext, FulfillmentDetails.class)
                                            .putExtras(bundle));
                                }

                            } else {
                                try {
                                    AppUtil.parseErrorMessage(mContext, response.errorBody().string());
                                } catch (IOException e) {
                                    AppUtil.toast(mContext, OOPS_SOMETHING_WENT_WRONG);
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<BroadCast>> call, Throwable t) {
                            Common.disMissDialog();
                            AppUtil.toast(mContext, OOPS_SOMETHING_WENT_WRONG);
                        }
                    });

        } else {
            AppUtil.toast(mContext, mContext.getString(R.string.check_interent_connection));
        }
    }

}
