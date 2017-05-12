package com.biglynx.fulfiller.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.PaymentDetailsModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

public class PaymentDetailsFragment extends Fragment {
    private ListView listview;
    private MyCustomAdapter adapter;
    private List<PaymentDetailsModel> list;
    private TextView noPayments_tv;
    public static final String PAYOUT_DETAILS = "interestDetails";
    private FullFillerApiWrapper apiWrapper;
    private static final String TAG = "PaymentDetails";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_details_fragment, null);
        initViews(view);
        callService();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list != null && list.size() > 0) {
                    callInterestDetailsService(list.get(position).InterestId, position);
                }
            }
        });

        return view;
    }

    private void callInterestDetailsService(String interestId, final int position) {
        if (!Common.isNetworkAvailable(getActivity())) {
            AppUtil.toast(getActivity(), getString(R.string.check_interent_connection));
            return;
        }
        Common.showDialog(getActivity());
        apiWrapper.interestInfoCall(AppPreferences.getInstance(getActivity()).getSignInResult() != null ?
                        AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken") : "",
                interestId, new Callback<InterestDTO>() {
                    @Override
                    public void onResponse(Call<InterestDTO> call, Response<InterestDTO> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            InterestDTO interestDTO = response.body();
                            PaymentDetailsModel model = list.get(position);
                            model.ordersCount = (interestDTO.Fulfillments != null) ? interestDTO.Fulfillments.OrderCount : "";
                            model.totalWeight = (interestDTO.Fulfillments != null) ? interestDTO.Fulfillments.TotalWeight : "";
                            model.FulfillmentId = (interestDTO.Fulfillments != null) ? interestDTO.Fulfillments.FulfillmentId : "";
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(PAYOUT_DETAILS, model);
                            startActivity(new Intent(getActivity(), PaymentsDetailInfoActivity.class)
                                    .putExtras(bundle));
                        } else {
                            try {
                                AppUtil.parseErrorMessage(getActivity(), response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(getActivity(), OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<InterestDTO> call, Throwable t) {
                        Common.disMissDialog();
                        Log.e(TAG, "interest details error :: " + t.getStackTrace());
                    }
                });

    }

    private void callService() {

        if (!Common.isNetworkAvailable(getActivity())) {
            AppUtil.toast(getActivity(), getString(R.string.check_interent_connection));
            return;
        }
        Common.showDialog(getActivity());
        apiWrapper.payoutsCall(AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken"), new Callback<List<PaymentDetailsModel>>() {
            @Override
            public void onResponse(Call<List<PaymentDetailsModel>> call, Response<List<PaymentDetailsModel>> response) {
                Common.disMissDialog();
                if (response.isSuccessful()) {
                    List<PaymentDetailsModel> detailsModels = response.body();
                    list = detailsModels;
                    sortListItems();
                    adapter.notifyDataSetChanged();
                    if (list == null || list.size() == 0) {
                        noPayments_tv.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                    }

                } else {
                    listview.setVisibility(View.GONE);
                    noPayments_tv.setVisibility(View.VISIBLE);
                    try {
                        AppUtil.parseErrorMessage(getActivity(), response.errorBody().string());
                    } catch (IOException e) {
                        AppUtil.toast(getActivity(), OOPS_SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                    }
                    AppUtil.CheckErrorCode(getActivity(), response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PaymentDetailsModel>> call, Throwable t) {
                Common.disMissDialog();
                noPayments_tv.setVisibility(View.VISIBLE);
                //AppUtil.toast(getContext(), OOPS_SOMETHING_WENT_WRONG);
            }
        });
    }

    private void sortListItems() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Collections.sort(list, new Comparator<PaymentDetailsModel>() {
            @Override
            public int compare(PaymentDetailsModel result1, PaymentDetailsModel result2) {
                try {
                    Date date1 = simpleDateFormat.parse(result1.DateCreated);
                    Date date2 = simpleDateFormat.parse(result2.DateCreated);
                    return date2.compareTo(date1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    private void initViews(View view) {
        apiWrapper = new FullFillerApiWrapper();
        listview = (ListView) view.findViewById(R.id.paymentsDetails_lv);
        noPayments_tv = (TextView) view.findViewById(R.id.noPayments_tv);
        list = new ArrayList<>();

        adapter = new MyCustomAdapter();
        listview.setAdapter(adapter);
    }

    private class MyCustomAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return (list != null && list.size() > 0) ? list.get(position) : null;
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_waiting_items, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            mViewHolder.retailer_name_tv.setText(list.get(position).RetailerName);
            mViewHolder.due_tv.setText("PayoutStatus: " + list.get(position).PayoutStatus);
            mViewHolder.price_tv.setText("$ " + AppUtil.getTwoDecimals(list.get(position).PayoutAmount));

            return convertView;
        }

        private class MyViewHolder {
            TextView expired_tv, retailer_name_tv, due_tv, price_tv;

            public MyViewHolder(View itemView) {
                expired_tv = (TextView) itemView.findViewById(R.id.expired_tv);
                expired_tv.setVisibility(View.GONE);
                retailer_name_tv = (TextView) itemView.findViewById(R.id.retailer_name_tv);
                due_tv = (TextView) itemView.findViewById(R.id.due_tv);
                price_tv = (TextView) itemView.findViewById(R.id.price_tv);
            }
        }
    }

}
