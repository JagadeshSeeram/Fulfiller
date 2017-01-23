package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class RetailerLocation implements Parcelable{
    public RetailerLocationAddress RetailerLocationAddress;
    public Retailer Retailer;
    public String LocationContactPerson;
    public String RetailerLocationId;
    public String LocationType;
    public String LocatedInMall;
    public String LocationContactRole;
    public String LocationContactPhone;
    public String LocationContactMail;

    protected RetailerLocation(Parcel in) {
        RetailerLocationAddress = in.readParcelable(com.biglynx.fulfiller.models.RetailerLocationAddress.class.getClassLoader());
        LocationContactPerson = in.readString();
        RetailerLocationId = in.readString();
        LocationType = in.readString();
        LocatedInMall = in.readString();
        LocationContactRole = in.readString();
        LocationContactPhone = in.readString();
        LocationContactMail = in.readString();
        Retailer = in.readParcelable(com.biglynx.fulfiller.models.Retailer.class.getClassLoader());
    }

    public static final Creator<RetailerLocation> CREATOR = new Creator<RetailerLocation>() {
        @Override
        public RetailerLocation createFromParcel(Parcel in) {
            return new RetailerLocation(in);
        }

        @Override
        public RetailerLocation[] newArray(int size) {
            return new RetailerLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(RetailerLocationAddress, i);
        parcel.writeString(LocationContactPerson);
        parcel.writeString(RetailerLocationId);
        parcel.writeString(LocationType);
        parcel.writeString(LocatedInMall);
        parcel.writeString(LocationContactRole);
        parcel.writeString(LocationContactPhone);
        parcel.writeString(LocationContactMail);
        parcel.writeParcelable(Retailer, i);
    }
}
