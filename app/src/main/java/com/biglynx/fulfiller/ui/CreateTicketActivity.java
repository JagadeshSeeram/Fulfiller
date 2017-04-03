package com.biglynx.fulfiller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.SupportCategoryModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.google.android.gms.vision.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

public class CreateTicketActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView companyname_tv;
    private ImageView icon_back;
    private ArrayList<SupportCategoryModel> categoriesList;
    private FullFillerApiWrapper apiWrapper;
    private EditText supportCategory_ev,description_ev;
    private LinearLayout caterory_LI;
    private TextView createTicket_tv;
    private String selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);
        initViews();
    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        companyname_tv.setText("CREATE SUPPORT TICKET");
        icon_back = (ImageView) findViewById(R.id.icon_back);
        icon_back.setVisibility(View.VISIBLE);
        companyname_tv.setVisibility(View.VISIBLE);
        apiWrapper = new FullFillerApiWrapper();
        supportCategory_ev = (EditText) findViewById(R.id.supportCategory_type_ev);
        description_ev = (EditText) findViewById(R.id.description_ev);
        caterory_LI = (LinearLayout) findViewById(R.id.supportCategory_LI);
        createTicket_tv = (TextView) findViewById(R.id.create_ticket_tv);

        createTicket_tv.setOnClickListener(this);
        supportCategory_ev.setOnClickListener(this);
        icon_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.supportCategory_type_ev:
                callSupportCategoriesService();
                break;
            case R.id.selectIssue_LI:
                break;
            case R.id.create_ticket_tv:
                if (AppUtil.ifNotEmpty(supportCategory_ev.getText().toString()) &&
                        AppUtil.ifNotEmpty(description_ev.getText().toString()))
                    callCreateTicketService();
                else
                    AppUtil.toast(CreateTicketActivity.this,"All fields are mandatory");
                break;
        }
    }

    private void callSupportCategoriesService() {
        if (!Common.isNetworkAvailable(CreateTicketActivity.this)){
            AppUtil.toast(CreateTicketActivity.this,"Network disconnected, Please check...");
            return;
        }
        Common.showDialog(this);
        String productCode = null;
        productCode = "BIGLYNX_DELIVERY_PARTNER";
        /*if (AppPreferences.getInstance(CreateTicketActivity.this).getSignInResult() != null){
            if (AppPreferences.getInstance(this).getSignInResult().optString("Role").equals("DeliveryPartner")) {
                rollType = "BIGLYNX_DELIVERY_PARTNER";
            } else {
                rollType = "BIGLYNX_DELIVERY_PERSON";
            }
        }*/

        apiWrapper.getSupportCategoriesCall(AppPreferences.getInstance(this).getSignInResult() != null ?
                        AppPreferences.getInstance(this).getSignInResult().optString("AuthNToken") : "",
                productCode, new Callback<ArrayList<SupportCategoryModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<SupportCategoryModel>> call, Response<ArrayList<SupportCategoryModel>> response) {
                        if (response.isSuccessful()) {
                            categoriesList = response.body();
                            if (categoriesList != null && categoriesList.size() > 0) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("categoriesList", categoriesList);
                                startActivityForResult(new Intent(CreateTicketActivity.this, VehicleType.class)
                                        .putExtras(bundle), 10);
                            }
                        } else {
                            try {
                                AppUtil.parseErrorMessage(CreateTicketActivity.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(CreateTicketActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            AppUtil.CheckErrorCode(CreateTicketActivity.this, response.code());
                        }
                        Common.disMissDialog();
                    }
                    @Override
                    public void onFailure(Call<ArrayList<SupportCategoryModel>> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(CreateTicketActivity.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                supportCategory_ev.setText(result);
                if (data.hasExtra("categoryID"))
                    selectedCategoryId = data.getStringExtra("categoryID");
            }
        }
    }

    public void callCreateTicketService(){
        if (!Common.isNetworkAvailable(CreateTicketActivity.this)){
            AppUtil.toast(CreateTicketActivity.this,"Network disconnected, Please check...");
            return;
        }
        Common.showDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("SupportCategoryId",selectedCategoryId != null ? selectedCategoryId : "");
        hashMap.put("Description",description_ev.getText().toString());
        hashMap.put("SupportPriority","High");
        hashMap.put("Status","New");
        hashMap.put("ProductCode","BIGLYNX_DELIVERY_PARTNER");

        apiWrapper.createTicketCall(AppPreferences.getInstance(this).getSignInResult() != null ?
                        AppPreferences.getInstance(this).getSignInResult().optString("AuthNToken") : "",
                hashMap, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            //SupportCategoryModel categoryModel = response.body();
                            AppUtil.toast(CreateTicketActivity.this,"Ticket created Successfully.");
                            finish();
                        }else {
                            try {
                                AppUtil.parseErrorMessage(CreateTicketActivity.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(CreateTicketActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                            AppUtil.CheckErrorCode(CreateTicketActivity.this, response.code());
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(CreateTicketActivity.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }
}
