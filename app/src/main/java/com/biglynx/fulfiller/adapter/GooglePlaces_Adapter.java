package com.biglynx.fulfiller.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.GooglePlaces;
import com.biglynx.fulfiller.models.RecentSearch;

import java.util.ArrayList;
import java.util.List;


public class GooglePlaces_Adapter extends BaseAdapter implements Filterable {
    Context Context;
    List<GooglePlaces> googlePlacesList;
    LayoutInflater inflater;
    private Filter filter;
    private List<GooglePlaces> suggestions;

    public GooglePlaces_Adapter(Context mContext, List<GooglePlaces> googleplacesLists){
        Context=mContext;
        googlePlacesList=googleplacesLists;
        suggestions = new ArrayList<>();
        inflater = LayoutInflater.from(this.Context);
        filter = new CustomFilter();
    }

    @Override
    public int getCount() {
        return suggestions.size();

    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
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

        mViewHolder.place_tv.setText(suggestions.get(position).description);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private static class MyViewHolder {
        TextView place_tv;

        public MyViewHolder(View item) {
            place_tv = (TextView) item.findViewById(R.id.place_tv);

        }
    }
    /**
     * Our Custom Filter Class.
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (googlePlacesList != null && constraint != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < googlePlacesList.size(); i++) {
                    if (googlePlacesList.get(i).description.toLowerCase().contains(constraint)) { // Compare item in original list if it contains constraints.
                        suggestions.add(googlePlacesList.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();
            Log.d("suggestions",""+suggestions.size());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            //notifyDataSetChanged();
            Log.d("resultcount",""+results.count);
            if (results != null && results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
