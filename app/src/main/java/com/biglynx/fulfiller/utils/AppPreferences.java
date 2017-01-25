package com.biglynx.fulfiller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.biglynx.fulfiller.models.AccountConfigModel;
import com.biglynx.fulfiller.models.SignInResult;

import org.json.JSONException;
import org.json.JSONObject;

public class AppPreferences {
    private static final String PREF_APP_RATE = "pref_app_rate";
    private static final String PREF_LAUNCH_COUNT = "pref_launch_count";
    private static final String SIGN_RESULT = "pref_app_rate";
    private static AppPreferences sInstance;
    private SharedPreferences mPrefs;
    private String ACCOUNt_INFO = "pref_account_info";
    private String NOTIFICATION_REGISTRATION_ID = "registration_ID";

    private AppPreferences(Context paramContext) {
        this.mPrefs = paramContext.getSharedPreferences("app_prefs", 0);
    }

    public static AppPreferences getInstance(Context paramContext) {
        if (sInstance == null) {
            sInstance = new AppPreferences(paramContext);
        }
        return sInstance;
    }

    public boolean getAppRate() {
        return this.mPrefs.getBoolean(PREF_APP_RATE, true);
    }


    public void setSignInResult(SignInResult signInResult) {
        Editor localEditor = this.mPrefs.edit();
        if (signInResult == null) {
            localEditor.putString(SIGN_RESULT, "");
        }else {
            localEditor.putString(SIGN_RESULT, signInResult.toJSON().toString());
        }
        localEditor.commit();
    }

    public void setAccountInfo(AccountConfigModel configModel){
        Editor localEditor = this.mPrefs.edit();
        if (configModel == null)
            localEditor.putString(ACCOUNt_INFO, "");
        else {
            localEditor.putString(ACCOUNt_INFO, configModel.toJson().toString());
        }
        localEditor.commit();
    }

    public JSONObject getAccountInfo(){
        String string = mPrefs.getString(ACCOUNt_INFO, "");
        try {
            JSONObject jsonObject = new JSONObject(string);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getSignInResult() {
        Editor localEditor = this.mPrefs.edit();
        String string = mPrefs.getString(SIGN_RESULT, "");
        try {
            JSONObject jsonObject = new JSONObject(string);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setAppRate(boolean paramBoolean) {
        Editor localEditor = this.mPrefs.edit();
        localEditor.putBoolean(PREF_APP_RATE, paramBoolean);
        localEditor.commit();
    }

    public int getLaunchCount() {
        return this.mPrefs.getInt(PREF_LAUNCH_COUNT, 0);
    }

    public void incrementLaunchCount() {
        int i = getLaunchCount();
        Editor localEditor = this.mPrefs.edit();
        localEditor.putInt(PREF_LAUNCH_COUNT, i + 1);
        localEditor.commit();
    }

    public void resetLaunchCount() {
        Editor localEditor = this.mPrefs.edit();
        localEditor.remove(PREF_LAUNCH_COUNT);
        localEditor.commit();
    }

    public void setRegistrationID(String registrationID){
        Editor localEditor = this.mPrefs.edit();
        localEditor.putString(NOTIFICATION_REGISTRATION_ID,registrationID);
        localEditor.commit();
    }

    public String getRegistrationID() {
        String registrationID = mPrefs.getString(NOTIFICATION_REGISTRATION_ID,null);
        if (registrationID != null)
            return registrationID;
        else
            return null;
    }
}
