package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/28/2016.
 */
public class BroadCast implements Parcelable {
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
    public List<FulfillersDTO> Fulfillments;
    public RetailerLocationAddress RetailerLocationAddress;
    public FulfillerInterests FulfillerInterests;
    public boolean isExpressed = false;
public boolean isClicked = false;

    public BroadCast(String ids,String s, String s1, String test1, String s2) {
        this.lat=s;
        this.id=ids;
        this.lang=s1;
        this.name=test1;
        this.noOfFullmets=s2;
    }

    protected BroadCast(Parcel in) {
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
        Fulfillments = in.createTypedArrayList(FulfillersDTO.CREATOR);
        RetailerLocationAddress = in.readParcelable(com.biglynx.fulfiller.models.RetailerLocationAddress.class.getClassLoader());
        FulfillerInterests = in.readParcelable(com.biglynx.fulfiller.models.FulfillerInterests.class.getClassLoader());
        isExpressed = in.readByte() != 0;
        isClicked = in.readByte() != 0;
    }

    public static final Creator<BroadCast> CREATOR = new Creator<BroadCast>() {
        @Override
        public BroadCast createFromParcel(Parcel in) {
            return new BroadCast(in);
        }

        @Override
        public BroadCast[] newArray(int size) {
            return new BroadCast[size];
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
        parcel.writeTypedList(Fulfillments);
        parcel.writeParcelable(RetailerLocationAddress, i);
        parcel.writeParcelable(FulfillerInterests, i);
        parcel.writeByte((byte) (isExpressed ? 1 : 0));
        parcel.writeByte((byte) (isClicked ? 1 : 0));
    }
}
