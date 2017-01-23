package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class Fulfillments implements Parcelable{

    public String FulfillerId;
    public String  Status;
    public String RetailerName;
    public String OrderCount;
    public String TotalDistance;
    public String FulfillmentId;
    public String PriceType;
    public String  Amount;
    public String  ExpirationDateTime1;
    public String  DueDate;

    public double PickUpMapLatitude;
    public double PickUpMapLongitude;
    public String  MinDistance;
    public String  MaxDistance;
    public String  TotalWeight;
    public String  TotalPackages;
    public String  LocationContactPerson;
    public String  LocationContactRole;
    public String  LocationContactPhone;
    public String  LocationContactMail;
    public String  DateUpdated;
    public String TotalApproxTimeInSeconds;
    public String ExpirationDateTime;
    public  String InterestExpirationDateTime;
    public String FulfillerInterestId;
    public List<Orders> Orders;
    public String InterestDateTime;
    public FulfillerInterests FulfillerInterests;
    public RetailerLocation RetailerLocation;


    protected Fulfillments(Parcel in) {
        FulfillerId = in.readString();
        Status = in.readString();
        RetailerName = in.readString();
        OrderCount = in.readString();
        TotalDistance = in.readString();
        FulfillmentId = in.readString();
        PriceType = in.readString();
        Amount = in.readString();
        ExpirationDateTime1 = in.readString();
        DueDate = in.readString();
        PickUpMapLatitude = in.readDouble();
        PickUpMapLongitude = in.readDouble();
        MinDistance = in.readString();
        MaxDistance = in.readString();
        TotalWeight = in.readString();
        TotalPackages = in.readString();
        LocationContactPerson = in.readString();
        LocationContactRole = in.readString();
        LocationContactPhone = in.readString();
        LocationContactMail = in.readString();
        DateUpdated = in.readString();
        TotalApproxTimeInSeconds = in.readString();
        ExpirationDateTime = in.readString();
        InterestExpirationDateTime = in.readString();
        FulfillerInterestId = in.readString();
        Orders = in.createTypedArrayList(com.biglynx.fulfiller.models.Orders.CREATOR);
        InterestDateTime = in.readString();
        FulfillerInterests = in.readParcelable(com.biglynx.fulfiller.models.FulfillerInterests.class.getClassLoader());
        RetailerLocation = in.readParcelable(com.biglynx.fulfiller.models.RetailerLocation.class.getClassLoader());
    }

    public static final Creator<Fulfillments> CREATOR = new Creator<Fulfillments>() {
        @Override
        public Fulfillments createFromParcel(Parcel in) {
            return new Fulfillments(in);
        }

        @Override
        public Fulfillments[] newArray(int size) {
            return new Fulfillments[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(FulfillerId);
        parcel.writeString(Status);
        parcel.writeString(RetailerName);
        parcel.writeString(OrderCount);
        parcel.writeString(TotalDistance);
        parcel.writeString(FulfillmentId);
        parcel.writeString(PriceType);
        parcel.writeString(Amount);
        parcel.writeString(ExpirationDateTime1);
        parcel.writeString(DueDate);
        parcel.writeDouble(PickUpMapLatitude);
        parcel.writeDouble(PickUpMapLongitude);
        parcel.writeString(MinDistance);
        parcel.writeString(MaxDistance);
        parcel.writeString(TotalWeight);
        parcel.writeString(TotalPackages);
        parcel.writeString(LocationContactPerson);
        parcel.writeString(LocationContactRole);
        parcel.writeString(LocationContactPhone);
        parcel.writeString(LocationContactMail);
        parcel.writeString(DateUpdated);
        parcel.writeString(TotalApproxTimeInSeconds);
        parcel.writeString(ExpirationDateTime);
        parcel.writeString(InterestExpirationDateTime);
        parcel.writeString(FulfillerInterestId);
        parcel.writeTypedList(Orders);
        parcel.writeString(InterestDateTime);
        parcel.writeParcelable(FulfillerInterests, i);
        parcel.writeParcelable(RetailerLocation, i);
    }
}
