package com.biglynx.fulfiller.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;

public class TermsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TYPE = "TYPE";
    public static final int TERMS_OF_SERVICE = 1;
    public static final int PRIVACY_POLICY = 2;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt(TYPE);
        }
        String title, url;
        if (type == PRIVACY_POLICY) {
            title = "privacy policy";
            url = "https://biglynxnew.azurewebsites.net/#/privacyPolicy";
        } else {
            title = "terms of service";
            url = "https://biglynxnew.azurewebsites.net/#/termsofservice";
        }
        TextView titleText = (TextView) findViewById(R.id.companyname_tv);
        titleText.setText(title);
        ImageView iconBack = (ImageView) findViewById(R.id.icon_back);
        iconBack.setVisibility(View.VISIBLE);

        final WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        iconBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
