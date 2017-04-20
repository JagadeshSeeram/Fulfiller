package com.biglynx.fulfiller.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView icon_back, title_tv, reset_password_tv,signIn_tv;
    private EditText email_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();

        String string = getString(R.string.member);
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, string.indexOf("SIGN"), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), string.indexOf("SIGN"), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signIn_tv.setMovementMethod(LinkMovementMethod.getInstance());
        signIn_tv.setText(spannableString);
    }

    private void initViews() {
        icon_back = (TextView) findViewById(R.id.icon_back);
        title_tv = (TextView) findViewById(R.id.companyname_tv);
        reset_password_tv = (TextView) findViewById(R.id.reset_password);
        email_et = (EditText) findViewById(R.id.email_edit);
        signIn_tv = (TextView) findViewById(R.id.signIn_tv);

        title_tv.setVisibility(View.GONE);
        icon_back.setOnClickListener(this);
        reset_password_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.reset_password:
                if (!AppUtil.ifNotEmpty(email_et.getText().toString())) {
                    AppUtil.toast(ForgotPasswordActivity.this, "Please enter your email");
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_et.getText().toString().trim()).matches()) {
                    AppUtil.toast(this, "Email is not valid!!");
                    return;
                }
                callService();
                break;
        }
    }

    private void callService() {
        Common.showDialog(ForgotPasswordActivity.this);
        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        apiWrapper.resetPasswordCall(AppPreferences.getInstance(getApplicationContext()).getSignInResult() != null ?
                        AppPreferences.getInstance(getApplicationContext()).getSignInResult().optString("AuthNToken") : "",
                email_et.getText().toString().trim(), new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            AppUtil.toast(getApplicationContext(), "Please check your mail...");
                        }
                        else {
                            try {
                                AppUtil.parseErrorMessage(getApplicationContext(), response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(getApplicationContext(), getString(R.string.OOPS));
                                e.printStackTrace();
                            }
                            AppUtil.CheckErrorCode(ForgotPasswordActivity.this, response.code());
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Common.disMissDialog();
                        Log.e("ForGotPwd :: ","Forgot password error");
                        t.printStackTrace();
                    }
                });
    }
}
