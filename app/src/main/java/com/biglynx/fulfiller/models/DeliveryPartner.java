package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snehitha.chinnapally on 1/18/2017.
 */

public class DeliveryPartner implements Parcelable{
    public String FulfillerId;
    public String PartnerStatus;
    public String Contactperson;
    public String JobTitle;
    public String Phone;
    public String ConfirmationCode;

    protected DeliveryPartner(Parcel in) {
        FulfillerId = in.readString();
        PartnerStatus = in.readString();
        Contactperson = in.readString();
        JobTitle = in.readString();
        Phone = in.readString();
        ConfirmationCode = in.readString();
    }

    public static final Creator<DeliveryPartner> CREATOR = new Creator<DeliveryPartner>() {
        @Override
        public DeliveryPartner createFromParcel(Parcel in) {
            return new DeliveryPartner(in);
        }

        @Override
        public DeliveryPartner[] newArray(int size) {
            return new DeliveryPartner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(FulfillerId);
        parcel.writeString(PartnerStatus);
        parcel.writeString(Contactperson);
        parcel.writeString(JobTitle);
        parcel.writeString(Phone);
        parcel.writeString(ConfirmationCode);
    }
}
