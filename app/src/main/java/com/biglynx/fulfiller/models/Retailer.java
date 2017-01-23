package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class Retailer implements Parcelable{

    public String BusinessLegalName;
    public String CompanyEmail;
    public String Phone;
    public String CompanyLogo;

    protected Retailer(Parcel in) {
        BusinessLegalName = in.readString();
        CompanyEmail = in.readString();
        Phone = in.readString();
        CompanyLogo = in.readString();
    }

    public static final Creator<Retailer> CREATOR = new Creator<Retailer>() {
        @Override
        public Retailer createFromParcel(Parcel in) {
            return new Retailer(in);
        }

        @Override
        public Retailer[] newArray(int size) {
            return new Retailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BusinessLegalName);
        parcel.writeString(CompanyEmail);
        parcel.writeString(Phone);
        parcel.writeString(CompanyLogo);
    }
}
