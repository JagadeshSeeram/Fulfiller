package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.RatingsModel;
import com.biglynx.fulfiller.models.SupportCategoryModel;
import com.biglynx.fulfiller.ui.RatingsActivity;
import com.biglynx.fulfiller.utils.AppUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<RatingsModel> ratingsList;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");

    public RatingsAdapter(Context context) {
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ratingsList = new ArrayList<>();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_ratings_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        float rating = Float.parseFloat(AppUtil.ifNotEmpty(ratingsList.get(position).Rating) ?
                ratingsList.get(position).Rating : "0");
        holder.mRatingBar.setRating(rating);
        holder.name_tv.setText("BY" + ratingsList.get(position).RetaielrName);
        if (AppUtil.ifNotEmpty(ratingsList.get(position).Comments))
            holder.descriptio_tv.setText("BY" + ratingsList.get(position).Comments);
        else
            holder.descriptio_tv.setVisibility(View.GONE);
        holder.date_tv.setText(formatedDate(ratingsList.get(position).DateCreated));

    }

    private String formatedDate(String dateCreated) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date result = null;
        String resultString = "";
        try {
            result = simpleDateFormat.parse(dateCreated);
            resultString = dateFormat.format(result);
            return resultString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    public void setItemsList(ArrayList<RatingsModel> itemsList) {
        if (itemsList == null || itemsList.size() == 0)
            return;
        if (ratingsList != null && ratingsList.size() > 0)
            ratingsList = new ArrayList<>();

        ratingsList.addAll(itemsList);

        Collections.sort(ratingsList, new Comparator<RatingsModel>() {
            @Override
            public int compare(RatingsModel model_one, RatingsModel mode_two) {
                try {
                    Date date_one = simpleDateFormat.parse(model_one.DateCreated);
                    Date date_two = simpleDateFormat.parse(mode_two.DateCreated);
                    return date_two.compareTo(date_one);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar mRatingBar;
        TextView name_tv;
        TextView date_tv;
        TextView descriptio_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            descriptio_tv = (TextView) itemView.findViewById(R.id.description_tv);

            LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
           // stars.getDrawable(3).setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            //stars.getDrawable(4).setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
