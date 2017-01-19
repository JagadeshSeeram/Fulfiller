package com.biglynx.fulfiller.models;

public class EditProfileModel {

    public String UserId;
    public long FulfillerId;
    public String AuthNToken;
    public String FirstName;
    public String MiddleName;
    public String LastName;
    public String AddressLine1;
    public String AddressLine2;
    public String City;
    public String State;
    public String ZipCode;
    public String Country;
    public String Phone;
    public String Email;
    public String LoginProvider;
    public String ProviderKey;
    public String Role;
    public String Status;
    public String ProfileImage;
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
    public String Proximity;
    public String PingInterval;
    public boolean ReadyFulfill;

    @Override
    public String toString() {
        return "EditProfileModel {" +
                "FulfillerId:"+FulfillerId +","+
                "FirstName:"+FirstName +","+
                "Email:"+Email +","+
                "Role:"+Role +","+
                "Status:"+Status +","+
                "ReadyFulfill:"+ReadyFulfill +
                " }";
    }
}
