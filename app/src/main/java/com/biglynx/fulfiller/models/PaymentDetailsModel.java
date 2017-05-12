package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snehitha.chinnapally on 12/22/2016.
 */
public class PaymentDetailsModel implements Parcelable {

    public String Comments;
    public String DateCreated ;
    public String DateUpdated ;
    public String FulfillerId ;
    public String InterestId ;
    public double PayoutAmount ;
    public String PayoutDate ;
    public String PayoutId ;
    public String PayoutStatus ;
    public String PayoutreferenceId ;
    public String RetailerName ;
    public String FulfillmentId;
    public String CompletedDatetime;
    public String ordersCount;
    public String totalWeight;

    protected PaymentDetailsModel(Parcel in) {
        Comments = in.readString();
        DateCreated = in.readString();
        DateUpdated = in.readString();
        FulfillerId = in.readString();
        InterestId = in.readString();
        PayoutAmount = in.readDouble();
        PayoutDate = in.readString();
        PayoutId = in.readString();
        PayoutStatus = in.readString();
        PayoutreferenceId = in.readString();
        RetailerName = in.readString();
        FulfillmentId = in.readString();
        CompletedDatetime = in.readString();
        ordersCount = in.readString();
        totalWeight = in.readString();
    }

    public static final Creator<PaymentDetailsModel> CREATOR = new Creator<PaymentDetailsModel>() {
        @Override
        public PaymentDetailsModel createFromParcel(Parcel in) {
            return new PaymentDetailsModel(in);
        }

        @Override
        public PaymentDetailsModel[] newArray(int size) {
            return new PaymentDetailsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Comments);
        dest.writeString(DateCreated);
        dest.writeString(DateUpdated);
        dest.writeString(FulfillerId);
        dest.writeString(InterestId);
        dest.writeDouble(PayoutAmount);
        dest.writeString(PayoutDate);
        dest.writeString(PayoutId);
        dest.writeString(PayoutStatus);
        dest.writeString(PayoutreferenceId);
        dest.writeString(RetailerName);
        dest.writeString(FulfillmentId);
        dest.writeString(CompletedDatetime);
        dest.writeString(ordersCount);
        dest.writeString(totalWeight);
    }
}
