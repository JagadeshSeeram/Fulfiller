package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snehitha.chinnapally on 1/10/2017.
 */

public class Fulfillerdefaults implements Parcelable{
    public String FulfillerId;
    public String Proximity;
    public String PingInterval;
    public boolean ReadyFulfill;

    protected Fulfillerdefaults(Parcel in) {
        FulfillerId = in.readString();
        Proximity = in.readString();
        PingInterval = in.readString();
        ReadyFulfill = in.readByte() != 0;
    }

    public static final Creator<Fulfillerdefaults> CREATOR = new Creator<Fulfillerdefaults>() {
        @Override
        public Fulfillerdefaults createFromParcel(Parcel in) {
            return new Fulfillerdefaults(in);
        }

        @Override
        public Fulfillerdefaults[] newArray(int size) {
            return new Fulfillerdefaults[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(FulfillerId);
        parcel.writeString(Proximity);
        parcel.writeString(PingInterval);
        parcel.writeByte((byte) (ReadyFulfill ? 1 : 0));
    }
}
