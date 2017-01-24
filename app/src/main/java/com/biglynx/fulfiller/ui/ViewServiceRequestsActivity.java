package com.biglynx.fulfiller.ui;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.ServiceRequestAdapter;
import com.biglynx.fulfiller.models.SupportCategoryModel;

import java.util.ArrayList;

public class ViewServiceRequestsActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ServiceRequestAdapter adapter;
    private ArrayList<SupportCategoryModel> categoriesList;
    private ImageView icon_back;
    private TextView companyname_tv;
    private int VERTICAL_ITEM_SPACE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service_requests);
        initViews();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_tickets);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        icon_back.setVisibility(View.VISIBLE);
        companyname_tv.setVisibility(View.VISIBLE);
        companyname_tv.setText("view service request");
        categoriesList = new ArrayList<>();

        adapter = new ServiceRequestAdapter(this);
        if (getIntent().hasExtra("categoriesList")){
            categoriesList = getIntent().getExtras().getParcelableArrayList("categoriesList");
        }
        adapter.setItemsList(categoriesList);
        mRecyclerView.setAdapter(adapter);

        icon_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }

    }
}
