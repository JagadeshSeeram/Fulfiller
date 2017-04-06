package com.biglynx.fulfiller.network;


import com.biglynx.fulfiller.models.AccountConfigModel;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillerInterests;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.FullfillerKpi;
import com.biglynx.fulfiller.models.InterestDTO;
import com.biglynx.fulfiller.models.MessagesModel;
import com.biglynx.fulfiller.models.NotificationRegisterModel;
import com.biglynx.fulfiller.models.PaymentDetailsModel;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.models.SupportCategoryModel;
import com.biglynx.fulfiller.models.UserProfile;
import com.biglynx.fulfiller.models.Vehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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


    @GET(HttpAdapter.FULLFILLER_KPI + "{fulfillerID}")
    Call<FullfillerKpi> fullfillerKpi(@Path("fulfillerID") String fullfillerId);


    @GET(HttpAdapter.DASHBOARD_NEW + "{fulfillerID}" + "/" + "{rollType}")
    Call<List<FulfillersDTO>> dashBoard(@Path("fulfillerID") String fullfillerId, @Path("rollType") String rollType);

    @FormUrlEncoded
    @POST(HttpAdapter.BASE_URL_LOGIN + "{url}")
    Call<SignInResult> editProfileCallInHome(@Path("url") String url, @Field("fulfillerid") Object fulfillerid, @Field("fulfillertype") Object fulfillertype,
                                             @Field("Proximity") Object Proximity, @Field("ReadyFulfill") Object ReadyFulfill);

    @FormUrlEncoded
    @POST(HttpAdapter.BASE_URL_LOGIN + "{url}")
    Call<SignInResult> editProfileInSettings(@Path("url") String url, @Field("BusinessLegalName") Object BusinessLegalName, @Field("FirstName") Object FirstName,
                                             @Field("Email") Object Email, @Field("AddressLine1") Object AddressLine1,
                                             @Field("City") Object City, @Field("State") Object State,
                                             @Field("Country") Object Country, @Field("Phone") Object Phone,
                                             @Field("BlobId") Object BlobId);

    @GET(HttpAdapter.INTERESTDETAILS + "/" + "{retaileLocId}")
    Call<InterestDTO> interestDetailsCall(@Path("retaileLocId") String retaileLocId);

    @GET(HttpAdapter.BROADCASt)
    Call<ArrayList<BroadCast>> broadCastCall();

    @GET(HttpAdapter.BROADCAStDETAILS + "/" + "{retaileLocId}")
    Call<ArrayList<BroadCast>> broadCastDetailsCall(@Path("retaileLocId") String retaileLocId);

    @POST(HttpAdapter.FULFILLERINTEREST)
    Call<FulfillerInterests> fulFillerInterestCall(@Body HashMap jsonObject);

    @GET(HttpAdapter.VEHICLE)
    Call<List<Vehicles>> vehicleListCall();

    @POST(HttpAdapter.VEHICLE)
    Call<Vehicles> addVehicleCall(@Body HashMap hashMap);

    @FormUrlEncoded
    @POST(HttpAdapter.DIRECTDEPOSIT)
    Call<AccountConfigModel> accountConfiguration(@Field("Fulfillerid") String Fulfillerid, @Field("BankName") String BankName, @Field("AccountType") String AccountType, @Field("AccountNumber") String AccountNumber,
                                                  @Field("RoutingNumber") String RoutingNumber, @Field("AccountName") String AccountName, @Field("Status") String Status);

    @GET(HttpAdapter.PAYOUTS)
    Call<List<PaymentDetailsModel>> payouts();

    @GET(HttpAdapter.START_DELIVERY + "/" + "{fulfillerid}" + "/" + "{confirmationcode}" + "/" + "{name}" + "/" + "{latitude}" + "/" + "{longitude}" + "/" + "{deviceid}")
    Call<InterestDTO> startDelivery(@Path("fulfillerid") Object fulfillerid, @Path("confirmationcode") Object confirmationcode, @Path("name") Object name,
                                    @Path("latitude") Object latitude, @Path("longitude") Object longitude, @Path("deviceid") Object deviceid);

    @GET(HttpAdapter.FULFILLMENT_PROGRESS_MESSAGE + "/" + "{FulFillmentId}")
    Call<List<MessagesModel>> getAllMessages(@Path("FulFillmentId") String FulFillmentId);

    @POST(HttpAdapter.FULFILLMENT_PROGRESS_MESSAGE)
    Call<List<MessagesModel>> postFulFillerMessages(@Body HashMap<String, Object> hashMap);

    @POST(HttpAdapter.UPDATE_DELIVERY_STATUS)
    Call<InterestDTO> updateDeliveryStatus(@Body HashMap<String, Object> hashMap);

    @GET(HttpAdapter.DIRECTDEPOSIT + "/" + "{fulfillerID}")
    Call<AccountConfigModel> getAccountDetails(@Path("fulfillerID") String fulfillerID);

    @GET(HttpAdapter.GET_SUPPORT_CATEGORIES)
    Call<ArrayList<SupportCategoryModel>> getSupportCategories(@Query("ProductCode") String productCode);

    @POST(HttpAdapter.CREATE_TICKET)
    Call<Void> createTicket(@Body HashMap<String, String> hashMap);

    @GET(HttpAdapter.CREATE_TICKET)
    Call<ArrayList<SupportCategoryModel>> getAllTickets(@Query("ProductCode") String productCode);

    @POST(HttpAdapter.NOTIFICATION_BACK_END)
    Call<String> sendFcmTokenToServer(@Query("productid") String productCode, @Query("handle") String fcmToken);

    @PUT(HttpAdapter.NOTIFICATION_BACK_END)
    Call<Void> sendRegistrationID(@Query("id") String registrationID, @Query("productid") String productCode,
                                  @Body NotificationRegisterModel model);

    @GET(HttpAdapter.BASE_URL + "{url}")
    Call<SignInResult> getProfile(@Path("url") String url);

    @FormUrlEncoded
    @POST(HttpAdapter.CANCEL_INTEREST)
    Call<FulfillerInterests> cancelInterest(@Field("FulfillerInterestId") String fulfillerInterestId);

    @GET(HttpAdapter.RESEND_ACTIVATION_MAIL)
    Call<Void> resendActivationMail();

    @GET(HttpAdapter.RESET_PASSWORD)
    Call<Void> resetPassword(@Query("UserEmail") String email);

}
