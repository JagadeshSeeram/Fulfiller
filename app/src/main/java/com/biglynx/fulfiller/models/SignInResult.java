package com.biglynx.fulfiller.models;


import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInResult {
    @SerializedName("AddressLine1")
    public String AddressLine1;
    @SerializedName("AddressLine2")
    public String AddressLine2;
    @SerializedName("AuthNToken")
    public String AuthNToken;
    @SerializedName("City")
    public String City;
    @SerializedName("Country")
    public String Country;
    @SerializedName("Email")
    public String Email;
    @SerializedName("FirstName")
    public String FirstName;
    @SerializedName("FulfillerId")
    public String FulfillerId;
    @SerializedName("LastName")
    public String LastName;
    @SerializedName("LoginProvider")
    public String LoginProvider;
    @SerializedName("MiddleName")
    public String MiddleName;
    @SerializedName("Phone")
    public String Phone;
    @SerializedName("PingInterval")
    public String PingInterval;
    @SerializedName("CompanyLogo")
    public String CompanyLogo;
    @SerializedName("Proximity")
    public String Proximity;
    @SerializedName("ReadyFulfill")
    public boolean ReadyFulfill;
    @SerializedName("Role")
    public String Role;
    @SerializedName("State")
    public String State;
    @SerializedName("Status")
    public String Status;
    @SerializedName("UserId")
    public String UserId;
    @SerializedName("ZipCode")
    public String ZipCode;
    @SerializedName("BusinessLegalName")
    public String BusinessLegalName;
    @SerializedName("ProviderKey")
    public String ProviderKey;
    @SerializedName("PersonId")
    public String PersonId;
    @SerializedName("IsPartnerAssociated")
    public String IsPartnerAssociated;
    @SerializedName("PartnerCompany")
    public String PartnerCompany;
    @SerializedName("BroadcastProximity")
    public String BroadcastProximity;
    @SerializedName("PartnerRefMemberId")
    public String PartnerRefMemberId;
    @SerializedName("DOB")
    public String DOB;
    @SerializedName("SSN")
    public String SSN;
    @SerializedName("AddressId")
    public String AddressId;
    @SerializedName("AcceptGPSAcknowledgement")
    public String AcceptGPSAcknowledgement;
    @SerializedName("FulfillerStatus")
    public String FulfillerStatus;
    @SerializedName("DateCreated")
    public String DateCreated;
    @SerializedName("UniqueDeviceId")
    public String UniqueDeviceId;
    @SerializedName("Description")
    public String Description;
    @SerializedName("BlobId")
    public String BlobId;
    @SerializedName("BusinessType")
    public String BusinessType;
    @SerializedName("NumberOfDeliveryVehicles")
    public String NumberOfDeliveryVehicles;
    @SerializedName("TaxID")
    public String TaxID;
    @SerializedName("StateIncorporated")
    public String StateIncorporated;
    @SerializedName("subscription")
    public Subscription subscription;
    //public boolean showNoticeDialog = false;

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AddressLine1", AddressLine1);
            jsonObject.put("AddressLine2", AddressLine2);
            jsonObject.put("AuthNToken", AuthNToken);
            jsonObject.put("City", City);
            jsonObject.put("Country", Country);
            jsonObject.put("Email", Email);
            jsonObject.put("FirstName", FirstName);
            jsonObject.put("FulfillerId", FulfillerId);
            jsonObject.put("LastName", LastName);
            jsonObject.put("LoginProvider", LoginProvider);
            jsonObject.put("MiddleName", MiddleName);
            jsonObject.put("Phone", Phone);
            jsonObject.put("PingInterval", PingInterval);
            jsonObject.put("CompanyLogo", CompanyLogo);
            jsonObject.put("Proximity", Proximity);
            jsonObject.put("ReadyFufill", ReadyFulfill);
            jsonObject.put("Role", Role);
            jsonObject.put("State", State);
            jsonObject.put("Status", Status);
            jsonObject.put("UserId", UserId);
            jsonObject.put("ZipCode", ZipCode);
            jsonObject.put("BusinessLegalName", BusinessLegalName);
            //jsonObject.put("showNoticeDialog",showNoticeDialog);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
