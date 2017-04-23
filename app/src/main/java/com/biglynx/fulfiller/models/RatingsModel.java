package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RatingsModel implements Parcelable{
    public String RatingId;
    public String FulfillmentId;
    public String RetaielrName;
    public String Comments;
    public String Rating;
    public String DateCreated;

    protected RatingsModel(Parcel in) {
        RatingId = in.readString();
        FulfillmentId = in.readString();
        RetaielrName = in.readString();
        Comments = in.readString();
        Rating = in.readString();
        DateCreated = in.readString();
    }

    public static final Creator<RatingsModel> CREATOR = new Creator<RatingsModel>() {
        @Override
        public RatingsModel createFromParcel(Parcel in) {
            return new RatingsModel(in);
        }

        @Override
        public RatingsModel[] newArray(int size) {
            return new RatingsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(RatingId);
        parcel.writeString(FulfillmentId);
        parcel.writeString(RetaielrName);
        parcel.writeString(Comments);
        parcel.writeString(Rating);
        parcel.writeString(DateCreated);
    }
}
