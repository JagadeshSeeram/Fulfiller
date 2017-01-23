package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Copyright (c) 2016 BigLynx
 * <p>
 * All Rights reserved.
 */

/*
* @author Ramakrishna on 8/18/2016
* @version 1.0
*
*/

public class FulfillerInterests implements Parcelable {
    public String FulfillerInterestId;
    public String FulfillmentId;
    public String InterestStatus;
    public String FulfillerType;
    public String PriceType;
    public double Amount;
    public String InterestDateTime;
    public String InvoiceID;
    public String CancelledDatetime;
    public String DateUpdated;
    public  String ConfirmationCode;
    public  String FulfillerId;
    public String InterestExpirationDateTime;

    protected FulfillerInterests(Parcel in) {
        FulfillerInterestId = in.readString();
        FulfillmentId = in.readString();
        InterestStatus = in.readString();
        FulfillerType = in.readString();
        PriceType = in.readString();
        Amount = in.readDouble();
        InterestDateTime = in.readString();
        InvoiceID = in.readString();
        CancelledDatetime = in.readString();
        DateUpdated = in.readString();
        ConfirmationCode = in.readString();
        FulfillerId = in.readString();
        InterestExpirationDateTime = in.readString();
    }

    public static final Creator<FulfillerInterests> CREATOR = new Creator<FulfillerInterests>() {
        @Override
        public FulfillerInterests createFromParcel(Parcel in) {
            return new FulfillerInterests(in);
        }

        @Override
        public FulfillerInterests[] newArray(int size) {
            return new FulfillerInterests[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(FulfillerInterestId);
        parcel.writeString(FulfillmentId);
        parcel.writeString(InterestStatus);
        parcel.writeString(FulfillerType);
        parcel.writeString(PriceType);
        parcel.writeDouble(Amount);
        parcel.writeString(InterestDateTime);
        parcel.writeString(InvoiceID);
        parcel.writeString(CancelledDatetime);
        parcel.writeString(DateUpdated);
        parcel.writeString(ConfirmationCode);
        parcel.writeString(FulfillerId);
        parcel.writeString(InterestExpirationDateTime);
    }
}
