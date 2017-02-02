package com.biglynx.fulfiller.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.biglynx.fulfiller.models.NotificationRegisterModel;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.ui.LoginActivity;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.models.NotificationRegisterModel.*;


public class RegistrationService extends Service {
    private static final String TAG = "RegIntentService";
    private Context mContext;
    private String registrationID;
    private FullFillerApiWrapper fillerApiWrapper;

    public RegistrationService(){

    }

    public RegistrationService(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fillerApiWrapper = new FullFillerApiWrapper();
        String fireBaseRegistrationID = FirebaseInstanceId.getInstance().getToken();
        if (fireBaseRegistrationID != null){
            Log.e(TAG,"FireBase Token :: "+fireBaseRegistrationID);
            sendFcmTokenToServer(fireBaseRegistrationID);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendFcmTokenToServer(final String fcmToken) {

        fillerApiWrapper.sendFcmTokenToServerCall(AppPreferences.getInstance(this).getSignInResult() != null ?
                        AppPreferences.getInstance(this).getSignInResult().optString("AuthNToken") : "",
                fcmToken, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            registrationID = response.body();
                            Log.e(TAG,"Sending FcmToken Body :: "+registrationID);
                            sendRegistrationID(registrationID,fcmToken);
                        }else {
                            try {
                                Log.e(TAG,"Sending FcmToken to Server :: "+response.errorBody().string());
                                //AppUtil.parseErrorMessage(LoginActivity.this, response.errorBody().string());
                            } catch (IOException e) {
                                Log.e(TAG,"Sending FcmToken Error");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG,"Sending FcmToken Error");
                        stopSelf();
                    }
                });
    }


    private void sendRegistrationID(final String registrationID, String fcmToken) {



        //JSONArray flagsObj = new JSONArray();
        String tag = null;
        if (AppPreferences.getInstance(this).getSignInResult() != null) {
            if (AppPreferences.getInstance(this).getSignInResult().optString("Role").equals("DeliveryPartner")) {
                tag = AppPreferences.getInstance(this).getSignInResult().optString("BusinessLegalName") != null ?
                        "UserName:"+AppPreferences.getInstance(this).getSignInResult().optString("BusinessLegalName") : "";
                /*flagsObj.put(AppPreferences.getInstance(this).getSignInResult().optString("BusinessLegalName") != null ?
                        "UserName:"+AppPreferences.getInstance(this).getSignInResult().optString("BusinessLegalName") : "");*/
            } else {
                tag = AppPreferences.getInstance(this).getSignInResult().optString("Email") != null ?
                        "UserName:"+AppPreferences.getInstance(this).getSignInResult().optString("Email") : "";
                /*flagsObj.put(AppPreferences.getInstance(this).getSignInResult().optString("Email") != null ?
                        "UserName:"+AppPreferences.getInstance(this).getSignInResult().optString("Email") : "");*/
            }
        }
        NotificationRegisterModel model = new NotificationRegisterModel();
        model.setPlatform("gcm");
        model.setHandle(fcmToken);

//        NotificationRegisterModel.TagsArray tagsArray = new NotificationRegisterModel.TagsArray();
//        tagsArray.setTag(tag != null ? tag : "");
        List<String> arrays = new ArrayList<>();
        arrays.add(tag != null ? tag : "");

        model.setTags(arrays);
        /*HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Platform","gcm");
        hashMap.put("Handle",fcmToken);
        hashMap.put("Tags",flagsObj);
*/
        if (registrationID != null){
            fillerApiWrapper.sendRegistrationID(AppPreferences.getInstance(this).getSignInResult() != null ?
                            AppPreferences.getInstance(this).getSignInResult().optString("AuthNToken") : "",
                    registrationID, model, new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()){
                                Log.e(TAG,"Notification Register Body :: "+response.body());
                                AppPreferences.getInstance(RegistrationService.this).setRegistrationID(registrationID);
                            }else {
                                try {
                                    AppUtil.parseErrorMessage(RegistrationService.this, response.errorBody().string());
                                } catch (IOException e) {
                                    Log.e(TAG,"Notification Register Error");
                                    e.printStackTrace();
                                }
                            }
                            stopSelf();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG,"Notification Register Error");
                            stopSelf();
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Service stopped");
        super.onDestroy();
    }
}
