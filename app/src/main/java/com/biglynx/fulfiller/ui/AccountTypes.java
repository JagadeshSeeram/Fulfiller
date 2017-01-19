package com.biglynx.fulfiller.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;

import java.util.ArrayList;

public class AccountTypes extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private TextView title_tv;
    private ImageView icon_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_types);
        initViews();
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.listview_accountTypes);
        title_tv = (TextView) findViewById(R.id.companyname_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        title_tv.setText(getString(R.string.account_types));
        icon_back.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AccountTypes.this,
                R.layout.account_type_view);

        arrayAdapter.add(getString(R.string.savings));
        arrayAdapter.add(getString(R.string.current));
        arrayAdapter.add(getString(R.string.pending));

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        icon_back.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
        String accountType = (String) adapterView.getItemAtPosition(position);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", accountType);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
        }
    }
}
