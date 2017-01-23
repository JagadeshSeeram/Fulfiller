package com.biglynx.fulfiller.models;


import org.json.JSONException;
import org.json.JSONObject;

public class AccountConfigModel {
    public String DirectDepositId;
    public String Fulfillerid;
    public String AccountType;
    public String AccountName;
    public String AccountNumber;
    public String RoutingNumber;
    public String Status;
    public String DateCreated;
    public String DateUpdated;
    public String BankName;

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BankName",BankName);
            jsonObject.put("AccountType",AccountType);
            jsonObject.put("AccountNumber",AccountNumber);
            jsonObject.put("RoutingNumber",RoutingNumber);
            jsonObject.put("AccountName",AccountName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
