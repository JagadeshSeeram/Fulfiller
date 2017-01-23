package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class Orders implements Parcelable{

    public OrderAddress OrderAddress;
    public String OrderId;
    public String CustomerFirstName;
    public String CustomerLastName;
    public String CustomerPhone;
    public String NoOfPackages;
    public String PackageInfo;
    public String PackageSize;
    public String PackageWeight;
    public int DeliveryStatusId;
    public String Status;

    protected Orders(Parcel in) {
        OrderId = in.readString();
        CustomerFirstName = in.readString();
        CustomerLastName = in.readString();
        CustomerPhone = in.readString();
        NoOfPackages = in.readString();
        PackageInfo = in.readString();
        PackageSize = in.readString();
        PackageWeight = in.readString();
        DeliveryStatusId = in.readInt();
        Status = in.readString();
        OrderAddress = in.readParcelable(com.biglynx.fulfiller.models.OrderAddress.class.getClassLoader());
    }

    public static final Creator<Orders> CREATOR = new Creator<Orders>() {
        @Override
        public Orders createFromParcel(Parcel in) {
            return new Orders(in);
        }

        @Override
        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(OrderId);
        parcel.writeString(CustomerFirstName);
        parcel.writeString(CustomerLastName);
        parcel.writeString(CustomerPhone);
        parcel.writeString(NoOfPackages);
        parcel.writeString(PackageInfo);
        parcel.writeString(PackageSize);
        parcel.writeString(PackageWeight);
        parcel.writeInt(DeliveryStatusId);
        parcel.writeString(Status);
        parcel.writeParcelable(OrderAddress, i);
    }
}
