package com.biglynx.fulfiller.network;


import com.biglynx.fulfiller.models.AccountConfigModel;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillerInterests;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.FullfillerKpi;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.MessagesModel;
import com.biglynx.fulfiller.models.PaymentDetailsModel;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.models.UserProfile;
import com.biglynx.fulfiller.models.Vehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FullFillerApi {


    @GET(HttpAdapter.LOGIN_SOCIAL)
    Call<SignInResult> socialLogin(@Query("LoginProvider") String email, @Query("ProviderKey") String password);

    @GET(HttpAdapter.LOGIN_EMAIl)
    Call<SignInResult> login(@Query("Email") String email, @Query("Password") String password);


    @POST(HttpAdapter.SIGNUP_DEVLIVERY_PARTNER)
    Call<SignInResult> registerDeliveryPartner(@Body UserProfile userProfile);

    @POST(HttpAdapter.SIGNUP_DRIVER)
    Call<SignInResult> registerDriver(@Body UserProfile userProfile);


    @GET(HttpAdapter.FULLFILLER_KPI+"{fulfillerID}")
    Call<FullfillerKpi> fullfillerKpi(@Path("fulfillerID") String fullfillerId);


    @GET(HttpAdapter.DASHBOARD_NEW+"{fulfillerID}"+"/"+"{rollType}")
    Call<List<FulfillersDTO>> dashBoard(@Path("fulfillerID") String fullfillerId, @Path("rollType") String rollType);


    @POST(HttpAdapter.BASE_URL+"{url}")
    Call<SignInResult> editProfileModelCall(@Path("url") String url, @Body HashMap<String, Object> jsonObject);

    @GET(HttpAdapter.INTERESTDETAILS+"/"+"{retaileLocId}")
    Call<InterestDTO> interestDetailsCall(@Path("retaileLocId") String retaileLocId);

    @GET(HttpAdapter.BROADCASt)
    Call<ArrayList<BroadCast>> broadCastCall();

    @GET(HttpAdapter.BROADCAStDETAILS+"/"+"{retaileLocId}")
    Call<ArrayList<BroadCast>> broadCastDetailsCall(@Path("retaileLocId") String retaileLocId);

    @POST(HttpAdapter.FULFILLERINTEREST)
    Call<FulfillerInterests> fulFillerInterestCall(@Body HashMap jsonObject);

    @GET(HttpAdapter.VEHICLE)
    Call<List<Vehicles>> vehicleListCall();

    @POST(HttpAdapter.VEHICLE)
    Call<Vehicles> addVehicleCall(@Body HashMap hashMap);

    @POST(HttpAdapter.DIRECTDEPOSIT)
    Call<AccountConfigModel> accountConfiguration(@Body HashMap<String,String> hashMap);

    @GET(HttpAdapter.PAYOUTS)
    Call<List<PaymentDetailsModel>> payouts();

    @GET(HttpAdapter.START_DELIVERY+"/"+"{fulfillerid}"+"/"+"{confirmationcode}"+"/"+"{name}"+"/"+"{latitude}"+"/"+"{longitude}"+"/"+"{deviceid}")
    Call<InterestDTO> startDelivery(@Path("fulfillerid") Object fulfillerid, @Path("confirmationcode") Object confirmationcode, @Path("name") Object name,
                                    @Path("latitude") Object latitude, @Path("longitude") Object longitude, @Path("deviceid") Object deviceid);

    @GET(HttpAdapter.FULFILLMENT_PROGRESS_MESSAGE+"/"+"{FulFillmentId}")
    Call<List<MessagesModel>> getAllMessages(@Path("FulFillmentId") String FulFillmentId);

    @POST(HttpAdapter.FULFILLMENT_PROGRESS_MESSAGE)
    Call<List<MessagesModel>> postFulFillerMessages(@Body HashMap<String,Object> hashMap);

    @POST(HttpAdapter.UPDATE_DELIVERY_STATUS)
    Call<InterestDTO> updateDeliveryStatus(@Body HashMap<String,Object> hashMap);
}
