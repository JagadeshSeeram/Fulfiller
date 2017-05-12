package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class FulfillersDTO implements Parcelable {

    public String FulfillerId;
    public String Status;
    public String RetailerName;
    public String OrderCount;
    public double TotalDistance;
    public String FulfillmentId;
    public String PriceType;
    public double Amount;
    public String BusinessLegalName;

    public String DueDate;

    public double PickUpMapLatitude;
    public double PickUpMapLongitude;
    public double MinDistance;
    public double MaxDistance;
    public String TotalWeight;
    public String TotalPackages;
    public String LocationContactPerson;
    public String LocationContactRole;
    public String LocationContactPhone;
    public String LocationContactMail;
    public String DateUpdated;
    public String TotalApproxTimeInSeconds;
    public String ExpirationDateTime;
    public String DeliveryDueDatetime;
    public String InterestExpirationDateTime;
    public String FulfillerInterestId;
    public List<Orders> Orders;
    public String InterestDateTime;
    public FulfillerInterests FulfillerInterests;
    public RetailerLocation RetailerLocation;
    public Fulfillments Fulfillments;
    public Fulfillerdefaults Fulfillerdefaults;
    public int DeliveryStatusId;
    public String DeliveryStatus;
    public long FulfillmentProgressId;
    public DeliveryPartner DeliveryPartner;
    public DeliveryPartner DeliveryPerson;

    protected FulfillersDTO(Parcel in) {
        FulfillerId = in.readString();
        Status = in.readString();
        RetailerName = in.readString();
        OrderCount = in.readString();
        TotalDistance = in.readDouble();
        FulfillmentId = in.readString();
        PriceType = in.readString();
        Amount = in.readDouble();
        BusinessLegalName = in.readString();
        DueDate = in.readString();
        PickUpMapLatitude = in.readDouble();
        PickUpMapLongitude = in.readDouble();
        MinDistance = in.readDouble();
        MaxDistance = in.readDouble();
        TotalWeight = in.readString();
        TotalPackages = in.readString();
        LocationContactPerson = in.readString();
        LocationContactRole = in.readString();
        LocationContactPhone = in.readString();
        LocationContactMail = in.readString();
        DateUpdated = in.readString();
        TotalApproxTimeInSeconds = in.readString();
        ExpirationDateTime = in.readString();
        DeliveryDueDatetime = in.readString();
        InterestExpirationDateTime = in.readString();
        FulfillerInterestId = in.readString();
        Orders = in.createTypedArrayList(com.biglynx.fulfiller.models.Orders.CREATOR);
        InterestDateTime = in.readString();
        FulfillerInterests = in.readParcelable(com.biglynx.fulfiller.models.FulfillerInterests.class.getClassLoader());
        RetailerLocation = in.readParcelable(com.biglynx.fulfiller.models.RetailerLocation.class.getClassLoader());
        Fulfillments = in.readParcelable(com.biglynx.fulfiller.models.Fulfillments.class.getClassLoader());
        Fulfillerdefaults = in.readParcelable(com.biglynx.fulfiller.models.Fulfillerdefaults.class.getClassLoader());
        DeliveryStatusId = in.readInt();
        DeliveryStatus = in.readString();
        FulfillmentProgressId = in.readLong();
        DeliveryPartner = in.readParcelable(com.biglynx.fulfiller.models.DeliveryPartner.class.getClassLoader());
        DeliveryPerson = in.readParcelable(com.biglynx.fulfiller.models.DeliveryPartner.class.getClassLoader());
    }

    public static final Creator<FulfillersDTO> CREATOR = new Creator<FulfillersDTO>() {
        @Override
        public FulfillersDTO createFromParcel(Parcel in) {
            return new FulfillersDTO(in);
        }

        @Override
        public FulfillersDTO[] newArray(int size) {
            return new FulfillersDTO[size];
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
        parcel.writeDouble(TotalDistance);
        parcel.writeString(FulfillmentId);
        parcel.writeString(PriceType);
        parcel.writeDouble(Amount);
        parcel.writeString(BusinessLegalName);
        parcel.writeString(DueDate);
        parcel.writeDouble(PickUpMapLatitude);
        parcel.writeDouble(PickUpMapLongitude);
        parcel.writeDouble(MinDistance);
        parcel.writeDouble(MaxDistance);
        parcel.writeString(TotalWeight);
        parcel.writeString(TotalPackages);
        parcel.writeString(LocationContactPerson);
        parcel.writeString(LocationContactRole);
        parcel.writeString(LocationContactPhone);
        parcel.writeString(LocationContactMail);
        parcel.writeString(DateUpdated);
        parcel.writeString(TotalApproxTimeInSeconds);
        parcel.writeString(ExpirationDateTime);
        parcel.writeString(DeliveryDueDatetime);
        parcel.writeString(InterestExpirationDateTime);
        parcel.writeString(FulfillerInterestId);
        parcel.writeTypedList(Orders);
        parcel.writeString(InterestDateTime);
        parcel.writeParcelable(FulfillerInterests, i);
        parcel.writeParcelable(RetailerLocation, i);
        parcel.writeParcelable(Fulfillments, i);
        parcel.writeParcelable(Fulfillerdefaults, i);
        parcel.writeInt(DeliveryStatusId);
        parcel.writeString(DeliveryStatus);
        parcel.writeLong(FulfillmentProgressId);
        parcel.writeParcelable(DeliveryPartner, i);
        parcel.writeParcelable(DeliveryPerson, i);
    }
}
