package com.biglynx.fulfiller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.Vehicles;
import com.biglynx.fulfiller.utils.AppUtil;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class VehiclesAdapter extends BaseAdapter {
    android.content.Context Context;
    List<Vehicles> vehiclesList;
    LayoutInflater inflater;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public VehiclesAdapter(Context mContext, List<Vehicles> mvehiclesList) {
        Context = mContext;
        vehiclesList = mvehiclesList;
        sortList();
        inflater = LayoutInflater.from(this.Context);
    }

    private void sortList() {
        Collections.sort(vehiclesList, new Comparator<Vehicles>() {
            @Override
            public int compare(Vehicles result1, Vehicles result2) {
                try{
                    Date date1 = simpleDateFormat.parse(result1.DateCreated);
                    Date date2 = simpleDateFormat.parse(result2.DateCreated);
                    return date2.compareTo(date1);
                }catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    @Override
    public int getCount() {
        return vehiclesList.size();
    }

    @Override
    public Object getItem(int position) {
        return vehiclesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.addvehicle_items, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.vehiclename_tv.setText(vehiclesList.get(position).VehicleType
                + ", " + vehiclesList.get(position).VehicleYear.trim()
                + " Make");
        mViewHolder.date_tv.setText("Licence plate number : "
                + vehiclesList.get(position).LicencePlateNumber);
        mViewHolder.licence_tv.setText("Added On "
                + AppUtil.getLocalDateFormat(vehiclesList.get(position).DateCreated));

        return convertView;
    }

    private class MyViewHolder {
        TextView vehiclename_tv, date_tv, licence_tv;
        ImageView vehicle_imv;

        public MyViewHolder(View item) {
            vehicle_imv = (ImageView) item.findViewById(R.id.vehicle_imv);
            vehiclename_tv = (TextView) item.findViewById(R.id.car_name_tv);
            date_tv = (TextView) item.findViewById(R.id.date_tv);
            licence_tv = (TextView) item.findViewById(R.id.licence_tv);
        }
    }
}
