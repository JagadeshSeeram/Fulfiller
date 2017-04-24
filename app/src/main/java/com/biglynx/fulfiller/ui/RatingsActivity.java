package com.biglynx.fulfiller.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.RatingsAdapter;
import com.biglynx.fulfiller.models.RatingsModel;
import com.biglynx.fulfiller.utils.AppUtil;

import java.util.ArrayList;

public class RatingsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mRatingsLI;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RatingsAdapter adapter;
    private ArrayList<RatingsModel> ratingsList;
    private ImageView icon_back;
    private TextView companyname_tv;
    private int VERTICAL_ITEM_SPACE = 40;
    private TextView total_ratings_tv;
    private RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service_requests);

        initViews();
    }

    private void initViews() {
        mRatingsLI = (LinearLayout) findViewById(R.id.total_ratings_LI);
        mRatingsLI.setVisibility(View.VISIBLE);
        total_ratings_tv = (TextView) findViewById(R.id.total_ratings_tv);
        ratingBar = (RatingBar) findViewById(R.id.total_ratingBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_tickets);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        icon_back.setVisibility(View.VISIBLE);
        companyname_tv.setVisibility(View.VISIBLE);
        companyname_tv.setText("reviews & ratings");

        ratingsList = new ArrayList<>();

        adapter = new RatingsAdapter(this);
        if (getIntent().hasExtra("ratingsList")){
            ratingsList = getIntent().getExtras().getParcelableArrayList("ratingsList");
        }
        adapter.setItemsList(ratingsList);
        mRecyclerView.setAdapter(adapter);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        //stars.getDrawable(3).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        //stars.getDrawable(4).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        total_ratings_tv.setText(ratingsList.size() + " retailer reviews");
        float netRating = 0;
        int count = 0;
        for (RatingsModel model : ratingsList){
            if (AppUtil.ifNotEmpty(model.Rating)) {
                netRating = netRating + Float.parseFloat(model.Rating);
                count = count + 1;
            }
        }

        netRating = count != 0 ? (netRating / count) : 0;
        ratingBar.setRating(netRating);

        icon_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_back:
                finish();
                break;
        }

    }
}
