package com.biglynx.fulfiller.network;

import android.util.Log;

import com.biglynx.fulfiller.models.AccountConfigModel;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillerInterests;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.FullfillerKpi;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.MessagesModel;
import com.biglynx.fulfiller.models.PaymentDetailsModel;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.models.SupportCategoryModel;
import com.biglynx.fulfiller.models.UserProfile;
import com.biglynx.fulfiller.models.Vehicles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FullFillerApiWrapper {

    private FullFillerApi fullFillerApi;
    private FullFillerApi fullFillerApiHeader;
    private Retrofit retrofitHeader;
    private final Retrofit retrofit;

    public FullFillerApiWrapper() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                .baseUrl(HttpAdapter.BASE_URL).build();
        fullFillerApi = retrofit.create(FullFillerApi.class);
    }

    private void createRetrofitWithHeader(final String authToken) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", authToken).addHeader("Key", HttpAdapter.KEY_FOR_API).build();
                return chain.proceed(request);
            }
        });
        OkHttpClient okHttpClient = builder.build();
        retrofitHeader = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                .baseUrl(HttpAdapter.BASE_URL).build();
        fullFillerApiHeader = retrofitHeader.create(FullFillerApi.class);
    }


    public Call<SignInResult> login(String email, String password, Callback<SignInResult> callback) {
        Call<SignInResult> loginCall = fullFillerApi.login(email, password);
        loginCall.enqueue(callback);
        Log.d("URLS", loginCall.request().url().toString());
        return loginCall;
    }

    public Call<SignInResult> socialLogin(String loginProvider, String providerKey, Callback<SignInResult> callback) {
        Call<SignInResult> loginCall = fullFillerApi.socialLogin(loginProvider, providerKey);
        loginCall.enqueue(callback);
        Log.d("URLS", loginCall.request().url().toString());
        return loginCall;
    }

    public Call<SignInResult> registerDeliveryPartner(UserProfile userProfile, Callback<SignInResult> callback) {
        Call<SignInResult> userProfileCall = fullFillerApi.registerDeliveryPartner(userProfile);
        userProfileCall.enqueue(callback);
        Log.d("URLS", userProfileCall.request().url().toString());
        Log.d("SIGNUP_PARTNER :: ", userProfileCall.request().body().toString());
        return userProfileCall;
    }

    public Call<SignInResult> registerDriver(UserProfile userProfile, Callback<SignInResult> callback) {
        Call<SignInResult> userProfileCall = fullFillerApi.registerDriver(userProfile);
        userProfileCall.enqueue(callback);
        Log.d("URLS", userProfileCall.request().url().toString());
        Log.d("SIGNUP_DRIVER :: ", userProfileCall.request().body().toString());
        return userProfileCall;
    }

    public Call<FullfillerKpi> fullfillerKpi(final String authToken, String fullfillerId, Callback<FullfillerKpi> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<FullfillerKpi> userProfileCall = fullFillerApiHeader.fullfillerKpi(fullfillerId);
        userProfileCall.enqueue(callback);
        Log.d("URLS", userProfileCall.request().url().toString());
        return userProfileCall;
    }

    public Call<List<FulfillersDTO>> dashBoardModelCall(final String authToken, String fullfillerId, String rollType, Callback<List<FulfillersDTO>> callback) {

        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<List<FulfillersDTO>> dashBoardModelCall = fullFillerApiHeader.dashBoard(fullfillerId, rollType);
        dashBoardModelCall.enqueue(callback);
        Log.d("URLS", dashBoardModelCall.request().url().toString());
        return dashBoardModelCall;
    }

    public Call<SignInResult> editProfileModelCall(String authToken, String url, HashMap<String, Object> hashMap, Callback<SignInResult> callback) {
        if (fullFillerApiHeader == null)
            createRetrofitWithHeader(authToken);
        Call<SignInResult> profileModelCall = fullFillerApiHeader.editProfileModelCall(url, hashMap);
        profileModelCall.enqueue(callback);
        Log.d("URLS", profileModelCall.request().url().toString());
        Log.d("editprofile", "Body :: " + profileModelCall.request().body());
        return profileModelCall;
    }

    public Call<InterestDTO> interestInfoCall(final String authToken, String retailerLocalId, Callback<InterestDTO> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<InterestDTO> modelCall = fullFillerApiHeader.interestDetailsCall(retailerLocalId);
        modelCall.enqueue(callback);
        Log.d("URLS", modelCall.request().url().toString());
        return modelCall;
    }

    public Call<ArrayList<BroadCast>> broadCastCall(final String authToken, Callback<ArrayList<BroadCast>> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<ArrayList<BroadCast>> call = fullFillerApiHeader.broadCastCall();
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<ArrayList<BroadCast>> broadCastDetailsCall(final String authToken, String retaileLocId, Callback<ArrayList<BroadCast>> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<ArrayList<BroadCast>> call = fullFillerApiHeader.broadCastDetailsCall(retaileLocId);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<FulfillerInterests> fulfillerInterestsCall(final String authToken, HashMap hashMap, Callback<FulfillerInterests> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<FulfillerInterests> call = fullFillerApiHeader.fulFillerInterestCall(hashMap);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        Log.d("fulfillerinterest", "Body :: " + call.request().body());
        return call;
    }

    public Call<List<Vehicles>> vehicleListCall(final String authToken, Callback<List<Vehicles>> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<List<Vehicles>> call = fullFillerApiHeader.vehicleListCall();
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<Vehicles> addVehicleCall(final String authToken, HashMap hashMap, Callback<Vehicles> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<Vehicles> call = fullFillerApiHeader.addVehicleCall(hashMap);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<AccountConfigModel> accountConfigurationCall(final String authToken, AccountConfigModel configModel,
                                                             Callback<AccountConfigModel> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<AccountConfigModel> call = fullFillerApiHeader.accountConfiguration(configModel);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<List<PaymentDetailsModel>> payoutsCall(final String authToken, Callback<List<PaymentDetailsModel>> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<List<PaymentDetailsModel>> call = fullFillerApiHeader.payouts();
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<InterestDTO> startDeliveryCall(final String authToken, HashMap<String, Object> hashMap, Callback<InterestDTO> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<InterestDTO> call = fullFillerApiHeader.startDelivery(hashMap.get("fulfillerid"), hashMap.get("confirmationcode"), hashMap.get("name"),
                hashMap.get("latitude"), hashMap.get("longitude"), hashMap.get("deviceid"));
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<List<MessagesModel>> getAllMessagesCall(final String authToken, String FulFillmentId, Callback<List<MessagesModel>> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<List<MessagesModel>> call = fullFillerApiHeader.getAllMessages(FulFillmentId);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<List<MessagesModel>> postFulfillerMessagesCall(final String authToken, HashMap<String, Object> hashMap,
                                                               Callback<List<MessagesModel>> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<List<MessagesModel>> call = fullFillerApiHeader.postFulFillerMessages(hashMap);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<InterestDTO> updateDeliverYStatusCall(final String authToken,
                                                      HashMap<String, Object> hashMap, Callback<InterestDTO> callback) {
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<InterestDTO> call = fullFillerApiHeader.updateDeliveryStatus(hashMap);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<AccountConfigModel> getAccountDetailsCall(String authToken,String fulfillerID,Callback<AccountConfigModel> callback){
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<AccountConfigModel> call = fullFillerApiHeader.getAccountDetails(fulfillerID);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<ArrayList<SupportCategoryModel>> getSupportCategoriesCall(String authToken,String productCode,
                                                                          Callback<ArrayList<SupportCategoryModel>> callback){
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<ArrayList<SupportCategoryModel>> call = fullFillerApiHeader.getSupportCategories(productCode);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<SupportCategoryModel> createTicketCall(String authToken, HashMap<String,String> hashMap,
                                                     Callback<SupportCategoryModel> callback){
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<SupportCategoryModel> call = fullFillerApiHeader.createTicket(hashMap);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }

    public Call<ArrayList<SupportCategoryModel>> getAllTicketsCall(String authToken,String productCode,
                                                                          Callback<ArrayList<SupportCategoryModel>> callback){
        if (fullFillerApiHeader == null) {
            createRetrofitWithHeader(authToken);
        }
        Call<ArrayList<SupportCategoryModel>> call = fullFillerApiHeader.getSupportCategories(productCode);
        call.enqueue(callback);
        Log.d("URLS", call.request().url().toString());
        return call;
    }
}
