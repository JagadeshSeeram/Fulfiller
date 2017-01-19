package com.biglynx.fulfiller.models;


import org.json.JSONException;
import org.json.JSONObject;

public class SignInResult {
    public String AddressLine1;
    public String AddressLine2;
    public String AuthNToken;
    public String City;
    public String Country;
    public String Email;
    public String FirstName;
    public String FulfillerId;
    public String LastName;
    public String LoginProvider;
    public String MiddleName;
    public String Phone;
    public String PingInterval;
    public String CompanyLogo;
    public String Proximity;
    public boolean ReadyFulfill;
    public String Role;
    public String State;
    public String Status;
    public String UserId;
    public String ZipCode;
    public String BusinessLegalName;
    public String ProviderKey;
    public String PersonId;
    public String IsPartnerAssociated;
    public String PartnerCompany;
    public String BroadcastProximity;
    public String PartnerRefMemberId;
    public String DOB;
    public String SSN;
    public String AddressId;
    public String AcceptGPSAcknowledgement;
    public String FulfillerStatus;
    public String DateCreated;
    public String UniqueDeviceId;
    public String Description;
    public String BlobId;
    public String BusinessType;
    public String NumberOfDeliveryVehicles;
    public String TaxID;
    public String StateIncorporated;
    public Subscription subscription;


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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
