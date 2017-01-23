package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Biglynx on 7/26/2016.
 */

public class OrderAddress implements Parcelable{

    public String AddressLine1;
    public String AddressLine2;
    public String City;
    public String State;
    public String Zip;
    public String CountryName;
    public String Latitude;
    public String Longitude;
    public String Degrees;
    public String Minutes;
    public String Seconds;

    protected OrderAddress(Parcel in) {
        AddressLine1 = in.readString();
        AddressLine2 = in.readString();
        City = in.readString();
        State = in.readString();
        Zip = in.readString();
        CountryName = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
        Degrees = in.readString();
        Minutes = in.readString();
        Seconds = in.readString();
    }

    public static final Creator<OrderAddress> CREATOR = new Creator<OrderAddress>() {
        @Override
        public OrderAddress createFromParcel(Parcel in) {
            return new OrderAddress(in);
        }

        @Override
        public OrderAddress[] newArray(int size) {
            return new OrderAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(AddressLine1);
        parcel.writeString(AddressLine2);
        parcel.writeString(City);
        parcel.writeString(State);
        parcel.writeString(Zip);
        parcel.writeString(CountryName);
        parcel.writeString(Latitude);
        parcel.writeString(Longitude);
        parcel.writeString(Degrees);
        parcel.writeString(Minutes);
        parcel.writeString(Seconds);
    }
}
