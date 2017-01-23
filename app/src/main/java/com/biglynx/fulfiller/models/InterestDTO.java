package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/28/2016.
 */
public class InterestDTO implements Parcelable {
    public String id;
    public String lat;
    public String lang;
    public String name;
    public String noOfFullmets;
    public String RetailerId;
    public String BusinessLegalName;
    public String CompanyEmail;
    public String Phone;
    public String CompanyLogo;
    public FulfillersDTO Fulfillments;
    public RetailerLocationAddress RetailerLocationAddress;
    public Orders Orders;


    public InterestDTO(String ids, String s, String s1, String test1, String s2) {
        this.lat = s;
        this.id = ids;
        this.lang = s1;
        this.name = test1;
        this.noOfFullmets = s2;
    }

    protected InterestDTO(Parcel in) {
        id = in.readString();
        lat = in.readString();
        lang = in.readString();
        name = in.readString();
        noOfFullmets = in.readString();
        RetailerId = in.readString();
        BusinessLegalName = in.readString();
        CompanyEmail = in.readString();
        Phone = in.readString();
        CompanyLogo = in.readString();
        Fulfillments = in.readParcelable(com.biglynx.fulfiller.models.FulfillersDTO.class.getClassLoader());
        RetailerLocationAddress = in.readParcelable(com.biglynx.fulfiller.models.RetailerLocationAddress.class.getClassLoader());
    }

    public static final Creator<InterestDTO> CREATOR = new Creator<InterestDTO>() {
        @Override
        public InterestDTO createFromParcel(Parcel in) {
            return new InterestDTO(in);
        }

        @Override
        public InterestDTO[] newArray(int size) {
            return new InterestDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(lat);
        parcel.writeString(lang);
        parcel.writeString(name);
        parcel.writeString(noOfFullmets);
        parcel.writeString(RetailerId);
        parcel.writeString(BusinessLegalName);
        parcel.writeString(CompanyEmail);
        parcel.writeString(Phone);
        parcel.writeString(CompanyLogo);
        parcel.writeParcelable(Fulfillments, i);
        parcel.writeParcelable(RetailerLocationAddress, i);
    }
}
