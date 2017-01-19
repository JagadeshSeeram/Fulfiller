/**
 * Copyright (C) 2016 BigLuynx
 * <p>
 * All rights reserved.
 */
package com.biglynx.fulfiller.network;


import android.util.Log;

public class HttpAdapter {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_UPDATE = "UPDATE";
    public static final String METHOD_PUT = "PUT";
    public static final String BASE_URL = "https://api.biglynx.com/broadcast/api/v1/";
    public static final String BASE_URL_LOGIN = "https://authz.biglynx.com/api/v1/";

    public static final String LOGIN_SOCIAL = BASE_URL_LOGIN + "BiglynxProviderLogin";
    //public static final String LOGIN_EMAIl = BASE_URL_LOGIN + "BiglynxLogin";
    public static final String LOGIN_EMAIl = BASE_URL_LOGIN + "biglynxAuthz";
    public static final String SIGNUP_DEVLIVERY_PARTNER = BASE_URL_LOGIN + "Deliverypartner/signup";
    public static final String SIGNUP_DRIVER = BASE_URL_LOGIN + "driver/signup";
    public static final String FULLFILLER_KPI = BASE_URL + "fulfillerkpi/";

    public static final String KEY_FOR_API = "QklHTFlOWEJST0FEQ0FTVDo6RDEzMUQzODMtQjVFNi00Mjk3LTkxMjItNjU1NkNGM0U0N0UxOjpmdWxmaWxsZXIuaW9zLmFwcDo6MTo6MTo6MDo6MDo6MDo6";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String DASHBOARD = BASE_URL + "dashboard/164473/Partner";
    public static final String DASHBOARDFIELDS = BASE_URL + "fulfillerkpi";
    public static final String BROADCASt = BASE_URL + "broadcastedfulfillments";
    public static final String BROADCAStDETAILS = BASE_URL + "broadcastedfulfillmentDetails";
    public static final String FULFILLERS = BASE_URL + "fulfillerinterest?";
    public static final String VEHICLE = BASE_URL + "vehicle";
    public static final String INTERESTDETAILS = BASE_URL + "interestdetails";
    public static final String EXTENDEXPIRATTION = BASE_URL + "extendexpiration";
    public static final String FULFILLERINTEREST = BASE_URL + "fulfillerinterest?";
    public static final String DIRECTDEPOSIT = BASE_URL + "directdeposit";

    public static final String DASHBOARD_NEW = BASE_URL + "dashboard/";
    public static final String PAYOUTS = BASE_URL + "payouts";
    public static final String START_DELIVERY = BASE_URL + "startdelivery/";
    public static final String FULFILLMENT_PROGRESS_MESSAGE = BASE_URL + "fulfillmentprogressmessage/";
    public static final String UPDATE_DELIVERY_STATUS = BASE_URL + "updatedeliverystatus?";


    // extendexpiration
    public static void extendexpiration(NetworkOperationListener listener, String tag, String jsonobject) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        Log.d("date", jsonobject.replace("\"", ""));
        operation.execute(EXTENDEXPIRATTION, METHOD_POST, jsonobject.replace("\"", ""));
    }

    // dashboard
    public static void dashboard(NetworkOperationListener listener, String tag) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(DASHBOARD, METHOD_GET, "");
    }

    // dashboard
    public static void dashboardfields(NetworkOperationListener listener, String tag, String id) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(DASHBOARDFIELDS + "/164473", METHOD_GET, "");
    }

    // get all vehicle
    public static void vehicle(NetworkOperationListener listener, String tag) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(VEHICLE, METHOD_GET, "");
    }

    // get all vehicle
    public static void interestDetails(NetworkOperationListener listener, String tag, String interestId) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(INTERESTDETAILS + "/" + interestId, METHOD_GET, "");
    }

    // fulfillments
    public static void fulfillments(NetworkOperationListener listener, String tag) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(FULFILLERS, METHOD_POST, "FulfillmentId=6807130167737454&FulfillerId=488961&PriceType=fixed&Amount=456454");
    }

    // addvehicle
    public static void addVehicle(NetworkOperationListener listener, String tag, String jsonobject) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(FULFILLERS, METHOD_POST, jsonobject);
    }

    //start delivery http://fulfillerapi.azurewebsites.net/v1/startdelivery/164473/84482290/Venkat/0.000000/0.000000/0
    public static void startDelivery(NetworkOperationListener listener, String tag, String token, String fulfillerId) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute("http://fulfillerapi.azurewebsites.net/v1/startdelivery/" + fulfillerId + "/" + token + "/Venkat/72.948341/89.344299/0", METHOD_GET, "");
    }

    //Broadcast
    public static void broadCast(NetworkOperationListener listener, String tag) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        operation.execute(BROADCASt, METHOD_GET, "");
    }

    //Broadcast details
    public static void broadCastDetails(NetworkOperationListener listener, String tag, String retailerLocId) {

        NetworkOperation operation = new NetworkOperation(listener, tag);
        operation.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        Log.d("ulr", BROADCAStDETAILS + "/" + retailerLocId);
        operation.execute(BROADCAStDETAILS + "/" + retailerLocId, METHOD_GET, "");
    }

}


