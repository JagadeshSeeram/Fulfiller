package com.biglynx.fulfiller.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessHours implements Parcelable {
    public String BusinessHoursId;
    public String RetailerId;
    public String BusinessHoursFriendlyName;
    public String MondayStart;
    public String MondayEnd;
    public String TuesdayStart;
    public String TuesdayEnd;
    public String WednesdayStart;
    public String WednesdayEnd;
    public String ThursdayStart;
    public String ThursdayEnd;
    public String FridayStart;
    public String FridayEnd;
    public String SaturdayStart;
    public String SaturdayEnd;
    public String SundayStart;
    public String SundayEnd;
    public String DateCreated;
    public String DateUpdated;

    protected BusinessHours(Parcel in) {
        BusinessHoursId = in.readString();
        RetailerId = in.readString();
        BusinessHoursFriendlyName = in.readString();
        MondayStart = in.readString();
        MondayEnd = in.readString();
        TuesdayStart = in.readString();
        TuesdayEnd = in.readString();
        WednesdayStart = in.readString();
        WednesdayEnd = in.readString();
        ThursdayStart = in.readString();
        ThursdayEnd = in.readString();
        FridayStart = in.readString();
        FridayEnd = in.readString();
        SaturdayStart = in.readString();
        SaturdayEnd = in.readString();
        SundayStart = in.readString();
        SundayEnd = in.readString();
        DateCreated = in.readString();
        DateUpdated = in.readString();
    }

    public static final Creator<BusinessHours> CREATOR = new Creator<BusinessHours>() {
        @Override
        public BusinessHours createFromParcel(Parcel in) {
            return new BusinessHours(in);
        }

        @Override
        public BusinessHours[] newArray(int size) {
            return new BusinessHours[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BusinessHoursId);
        parcel.writeString(RetailerId);
        parcel.writeString(BusinessHoursFriendlyName);
        parcel.writeString(MondayStart);
        parcel.writeString(MondayEnd);
        parcel.writeString(TuesdayStart);
        parcel.writeString(TuesdayEnd);
        parcel.writeString(WednesdayStart);
        parcel.writeString(WednesdayEnd);
        parcel.writeString(ThursdayStart);
        parcel.writeString(ThursdayEnd);
        parcel.writeString(FridayStart);
        parcel.writeString(FridayEnd);
        parcel.writeString(SaturdayStart);
        parcel.writeString(SaturdayEnd);
        parcel.writeString(SundayStart);
        parcel.writeString(SundayEnd);
        parcel.writeString(DateCreated);
        parcel.writeString(DateUpdated);
    }
}
