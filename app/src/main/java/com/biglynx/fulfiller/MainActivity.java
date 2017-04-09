package com.biglynx.fulfiller;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.biglynx.fulfiller.services.MyJobService;
import com.biglynx.fulfiller.ui.FulfillmentFragment;
import com.biglynx.fulfiller.ui.HomeFragment;
import com.biglynx.fulfiller.ui.BroadCastFragment;
import com.biglynx.fulfiller.ui.SettingsFragment;

public class MainActivity extends FragmentActivity {
    private static FragmentTabHost mTabHost;
    private static int kJobId = 0;
    ComponentName mServiceComponent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        if (mServiceComponent == null)
            startLocationService();
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);

        mTabHost.addTab(
                mTabHost.newTabSpec("HOME").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.home, R.drawable.ic_home,true)),
                HomeFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("BROADCAST").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.broadcast, R.drawable.ic_broadcasts,false)),
                BroadCastFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("FULFILLMENTS").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.fulfillment, R.drawable.ic_fulfillments,false)),
                FulfillmentFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("ME").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.me, R.drawable.ic_settings,false)),
                SettingsFragment.class, null);


        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTabs();
            }
        });
    }

    public static void setCurrentTab(int position){
        mTabHost.setCurrentTab(position);
    }

    public static void updateTabs() {
        int tab = mTabHost.getCurrentTab();

        for(int i=0;i<=3;i++){

            View v = mTabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) v.findViewById(R.id.textView);
            ImageView imv = (ImageView) v.findViewById(R.id.imageView);

            if(tab==i){

                tv.setTextColor(Color.parseColor("#EC932F"));
                if(i==0){
                    imv.setImageResource(R.drawable.ic_home_selected);
                }else  if(i==1){
                    imv.setImageResource(R.drawable.ic_broadcasts_selected);
                }else  if(i==2){
                    imv.setImageResource(R.drawable.ic_fulfillments_selected);
                }else  if(i==3){
                    imv.setImageResource(R.drawable.ic_settings_selected);
                }
            }
            else {
                tv.setTextColor(Color.parseColor("#B1B1B1"));
                if(i==0){
                    imv.setImageResource(R.drawable.ic_home);
                }else  if(i==1){
                    imv.setImageResource(R.drawable.ic_broadcasts);
                }else  if(i==2){
                    imv.setImageResource(R.drawable.ic_fulfillments);
                }else  if(i==3){
                    imv.setImageResource(R.drawable.ic_settings);
                }
            }
        }
    }

    private View getTabIndicator(Context context, int title, int icon, boolean intialtab) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setImageResource(icon);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        if(intialtab){
            tv.setTextColor(Color.parseColor("#EC932F"));
            iv.setImageResource(R.drawable.ic_home_selected);
        }
        return view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startLocationService() {
        mServiceComponent = new ComponentName(this, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, mServiceComponent);
        //builder.setMinimumLatency(120 * 1000); // wait at least
        builder.setOverrideDeadline(120 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void cancelAllJobs(View v) {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }
}