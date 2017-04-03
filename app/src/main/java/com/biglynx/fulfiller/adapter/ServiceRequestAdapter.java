package com.biglynx.fulfiller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.SupportCategoryModel;
import com.biglynx.fulfiller.utils.AppUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ViewHolder> {

    LayoutInflater inflater;
    ArrayList<SupportCategoryModel> categoryList;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");

    public ServiceRequestAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        categoryList = new ArrayList<>();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView status_tv,caterory_tv,issue_tv,date_tv,description_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            status_tv = (TextView) itemView.findViewById(R.id.category_status_tv);
            caterory_tv = (TextView) itemView.findViewById(R.id.category_type_tv);
            issue_tv = (TextView) itemView.findViewById(R.id.issue_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            description_tv = (TextView) itemView.findViewById(R.id.description_tv);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_service_request_item,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.status_tv.setText(categoryList.get(position).Status);
        holder.caterory_tv.setText(AppUtil.ifNotEmpty(categoryList.get(position).Category) ? "-"+categoryList.get(position).Category
        : "");
        holder.issue_tv.setText(AppUtil.ifNotEmpty(categoryList.get(position).Category) ? categoryList.get(position).CategoryTitle : "");
        holder.date_tv.setText(AppUtil.getLocalDateFormat(categoryList.get(position).CreatedDateTime));
        holder.description_tv.setText(categoryList.get(position).Description);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setItemsList(ArrayList<SupportCategoryModel> itemsList){
        if (itemsList == null || itemsList.size() == 0)
            return;
        if (categoryList != null)
            categoryList.clear();
      categoryList.addAll(itemsList);

        Collections.sort(categoryList, new Comparator<SupportCategoryModel>() {
            @Override
            public int compare(SupportCategoryModel model_one, SupportCategoryModel mode_two) {
                try {
                    Date date_one = simpleDateFormat.parse(model_one.CreatedDateTime);
                    Date date_two = simpleDateFormat.parse(mode_two.CreatedDateTime);
                    return date_two.compareTo(date_one);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        notifyDataSetChanged();
    }
}
