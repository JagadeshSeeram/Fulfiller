package com.biglynx.fulfiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;


public class CustomerSupport extends AppCompatActivity implements View.OnClickListener {

    ImageView icon_back;
    TextView companyname_tv;
    private TextView createTicket_tv,viewTicket_tv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customersupport);
        initViews();
    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        icon_back.setVisibility(View.VISIBLE);
        companyname_tv.setText("Customer Support");
        createTicket_tv = (TextView) findViewById(R.id.create_ticket_tv);
        viewTicket_tv = (TextView) findViewById(R.id.view_ticket_tv);

        createTicket_tv.setOnClickListener(this);
        viewTicket_tv.setOnClickListener(this);
        icon_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.create_ticket_tv:
                startActivity(new Intent(CustomerSupport.this,CreateTicketActivity.class));
                break;
            case R.id.view_ticket_tv:
                break;
        }
    }
}
