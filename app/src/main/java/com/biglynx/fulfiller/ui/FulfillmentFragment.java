package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.FulfillerConfirmAdapter;
import com.biglynx.fulfiller.adapter.FulfillerPendingAdapter;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

 /*
 *
 * Created by Biglynx on 7/21/2016.
 *
 */

public class FulfillmentFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    List<FulfillersDTO> compltedFulfillerList;
    List<FulfillersDTO> waitingFulfillerList;
    List<FulfillersDTO> confirmdFulfillerList;

    ListView fulfiment_lv;
    FulfillerConfirmAdapter fulfillerConfirmAdapter;
    TextView active_tv, past_tv, awating_tv, confirmed_tv, nofulfillments_tv;
    boolean completed = false, waiting = false, confirm = false;
    LinearLayout headerbar_LI;
    private String rollType;
    private FullFillerApiWrapper apiWrapper;
    FulfillerPendingAdapter fulfillerPendingAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.myfulfillment, container, false);

        initViews(v);

        if (AppPreferences.getInstance(getActivity()).getSignInResult() != null) {
            Log.e(FulfillmentFragment.class.getSimpleName(), "AuthToken :: " +
                    AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken"));
            if (AppPreferences.getInstance(getActivity()).getSignInResult().optString("Role").equals("DeliveryPartner")) {
                rollType = "Partner";
            } else {
                rollType = "Person";
            }
        }

        if (Common.isNetworkAvailable(getActivity())) {
            callService(true);
        } else
            AppUtil.toast(getActivity(), getString(R.string.check_interent_connection));


        fulfiment_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), InterestDetails.class);
                if (confirm) {
                    intent.putExtra("interestId", "" + confirmdFulfillerList.get(position).FulfillerInterestId);
                    intent.putExtra("completed", "completed");
                }
                if (waiting) {
                    intent.putExtra("interestId", "" + waitingFulfillerList.get(position).FulfillerInterestId);
                    intent.putExtra("completed", "not");
                }
                if (completed) {
                    intent.putExtra("interestId", "" + compltedFulfillerList.get(position).FulfillerInterestId);
                    intent.putExtra("completed", "not");
                }
                startActivity(intent);

            }
        });

        return v;
    }

    private void callService(final boolean showDialog) {
        //DashboardAPI (fulfillments)
        if (showDialog)
            Common.showDialog(getActivity());

        apiWrapper.dashBoardModelCall(AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken"),
                AppPreferences.getInstance(getActivity()).getSignInResult().optString("FulfillerId"),
                rollType, new Callback<List<FulfillersDTO>>() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call<List<FulfillersDTO>> call, Response<List<FulfillersDTO>> response) {
                        if (response.isSuccessful()) {
                            emptyAllLists();
                            List<FulfillersDTO> fulfillersDTOList = response.body();
                            for (int i = 0; i < fulfillersDTOList.size(); i++) {
                                FulfillersDTO fulfillersDTO = fulfillersDTOList.get(i);

                                if (fulfillersDTO.Status.equals("Confirmed")) {
                                    confirmdFulfillerList.add(fulfillersDTO);
                                } else if (fulfillersDTO.Status.equals("Expressed")) {
                                    waitingFulfillerList.add(fulfillersDTO);
                                } else {
                                    compltedFulfillerList.add(fulfillersDTO);
                                }
                            }
                            if (waitingFulfillerList.size() > 0) {
                                fulfiment_lv.setVisibility(View.VISIBLE);
                                nofulfillments_tv.setVisibility(View.GONE);
                                fulfillerConfirmAdapter = new FulfillerConfirmAdapter(getActivity(), waitingFulfillerList);
                                fulfiment_lv.setAdapter(fulfillerConfirmAdapter);
                                Common.setListViewHeightBasedOnItems(fulfiment_lv);
                                waiting = true;
                                completed = false;
                                confirm = false;
                            } else {
                                fulfiment_lv.setVisibility(View.GONE);
                                nofulfillments_tv.setVisibility(View.VISIBLE);
                            }

                        } else {
                            fulfiment_lv.setVisibility(View.GONE);
                            nofulfillments_tv.setVisibility(View.VISIBLE);
                            waiting = false;
                            completed = false;
                            confirm = false;
                            try {
                                AppUtil.parseErrorMessage(getActivity(), response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(getActivity(), OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }

                            AppUtil.CheckErrorCode(getActivity(), response.code());
                        }

                        if (showDialog)
                            Common.disMissDialog();
                        else {
                            setTabPs();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FulfillersDTO>> call, Throwable t) {
                        fulfiment_lv.setVisibility(View.GONE);
                        nofulfillments_tv.setVisibility(View.VISIBLE);
                        waiting = false;
                        completed = false;
                        confirm = false;
                        Log.e(FulfillmentFragment.class.getSimpleName(), "DashboardAPI :: " + t.getMessage());
                        // AppUtil.toast(getContext(), OOPS_SOMETHING_WENT_WRONG);
                        if (showDialog)
                            Common.disMissDialog();
                        else
                            swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void emptyAllLists() {
        if (confirmdFulfillerList != null && confirmdFulfillerList.size() > 0)
            confirmdFulfillerList.clear();
        if (waitingFulfillerList != null && waitingFulfillerList.size() > 0)
            waitingFulfillerList.clear();
        if (compltedFulfillerList != null && compltedFulfillerList.size() > 0)
            compltedFulfillerList.clear();
    }


    private void initViews(View v) {
        compltedFulfillerList = new ArrayList<>();
        waitingFulfillerList = new ArrayList<>();
        confirmdFulfillerList = new ArrayList<>();
        active_tv = (TextView) v.findViewById(R.id.active_tv);
        past_tv = (TextView) v.findViewById(R.id.past_tv);

        awating_tv = (TextView) v.findViewById(R.id.awating_tv);
        confirmed_tv = (TextView) v.findViewById(R.id.confirmed_tv);
        nofulfillments_tv = (TextView) v.findViewById(R.id.nofulfillments_tv);
        fulfiment_lv = (ListView) v.findViewById(R.id.fulfiment_lv);
        headerbar_LI = (LinearLayout) v.findViewById(R.id.headerbar_LI);

        past_tv.setOnClickListener(this);
        active_tv.setOnClickListener(this);

        awating_tv.setOnClickListener(this);
        confirmed_tv.setOnClickListener(this);

        setTabPs();

        apiWrapper = new FullFillerApiWrapper();

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_To_Refresh_Layout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setTabPs() {
        if (!headerbar_LI.isShown())
            headerbar_LI.setVisibility(View.VISIBLE);
        active_tv.setBackgroundResource(R.drawable.lef_roundedcorner);
        past_tv.setBackgroundResource(R.drawable.lef_roundedcorner_trans);
        active_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        past_tv.setTextColor(Color.parseColor("#FFFFFF"));
        awating_tv.setVisibility(View.VISIBLE);
        confirmed_tv.setVisibility(View.VISIBLE);
        confirmed_tv.setTextColor(Color.parseColor("#FFFFFF"));
        awating_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.active_tv:
                headerbar_LI.setVisibility(View.VISIBLE);
                active_tv.setBackgroundResource(R.drawable.lef_roundedcorner);
                past_tv.setBackgroundResource(R.drawable.lef_roundedcorner_trans);
                active_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                past_tv.setTextColor(Color.parseColor("#FFFFFF"));
                awating_tv.setVisibility(View.VISIBLE);
                confirmed_tv.setVisibility(View.VISIBLE);

                confirmed_tv.setTextColor(Color.parseColor("#FFFFFF"));
                awating_tv.setTextColor(getResources().getColor(R.color.colorPrimary));

                if (waitingFulfillerList.size() > 0) {
                    fulfiment_lv.setVisibility(View.VISIBLE);
                    nofulfillments_tv.setVisibility(View.GONE);
                    fulfillerConfirmAdapter = new FulfillerConfirmAdapter(getActivity(), waitingFulfillerList);
                    fulfiment_lv.setAdapter(fulfillerConfirmAdapter);
                    Common.setListViewHeightBasedOnItems(fulfiment_lv);
                    waiting = true;
                    confirm = false;
                    completed = false;

                } else {
                    fulfiment_lv.setVisibility(View.GONE);
                    nofulfillments_tv.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.past_tv:
                headerbar_LI.setVisibility(View.GONE);
                active_tv.setBackgroundResource(R.drawable.right_roundedcorner_trans);
                past_tv.setBackgroundResource(R.drawable.right_roundedcorner);
                past_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                active_tv.setTextColor(Color.parseColor("#FFFFFF"));
                awating_tv.setVisibility(View.GONE);
                confirmed_tv.setVisibility(View.GONE);

                if (compltedFulfillerList.size() > 0) {

                    fulfiment_lv.setVisibility(View.VISIBLE);
                    nofulfillments_tv.setVisibility(View.GONE);

                    FulfillerPendingAdapter fulfillerPendingAdapter = new FulfillerPendingAdapter(getActivity(), compltedFulfillerList, false);
                    fulfiment_lv.setAdapter(fulfillerPendingAdapter);
                    Common.setListViewHeightBasedOnItems(fulfiment_lv);
                    waiting = false;
                    confirm = false;
                    completed = true;

                } else {
                    fulfiment_lv.setVisibility(View.GONE);
                    nofulfillments_tv.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.awating_tv:

                confirmed_tv.setTextColor(Color.parseColor("#FFFFFF"));
                awating_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                if (waitingFulfillerList.size() > 0) {
                    fulfiment_lv.setVisibility(View.VISIBLE);
                    nofulfillments_tv.setVisibility(View.GONE);
                    fulfillerConfirmAdapter = new FulfillerConfirmAdapter(getActivity(), waitingFulfillerList);
                    fulfiment_lv.setAdapter(fulfillerConfirmAdapter);
                    Common.setListViewHeightBasedOnItems(fulfiment_lv);
                    waiting = true;
                    confirm = false;
                    completed = false;
                } else {
                    fulfiment_lv.setVisibility(View.GONE);
                    nofulfillments_tv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.confirmed_tv:

                awating_tv.setTextColor(Color.parseColor("#FFFFFF"));
                confirmed_tv.setTextColor(getResources().getColor(R.color.colorPrimary));

                if (confirmdFulfillerList.size() > 0) {
                    fulfiment_lv.setVisibility(View.VISIBLE);
                    nofulfillments_tv.setVisibility(View.GONE);
                    fulfillerConfirmAdapter = new FulfillerConfirmAdapter(getActivity(), confirmdFulfillerList);
                    fulfiment_lv.setAdapter(fulfillerConfirmAdapter);
                    Common.setListViewHeightBasedOnItems(fulfiment_lv);
                    waiting = false;
                    confirm = true;
                    completed = false;
                } else {
                    fulfiment_lv.setVisibility(View.GONE);
                    nofulfillments_tv.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        callService(false);
    }
}
