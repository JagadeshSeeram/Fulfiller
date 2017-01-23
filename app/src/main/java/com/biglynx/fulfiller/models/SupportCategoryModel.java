package com.biglynx.fulfiller.models;


import android.os.Parcel;
import android.os.Parcelable;

public class SupportCategoryModel implements Parcelable {
    public int SupportCategoryId;
    public String Category;
    public String CategoryTitle;
    public String SupportRequestId;
    public String UserName;
    public String Description;
    public String SupportPriority;
    public String Status;
    public String CreatedDateTime;

    protected SupportCategoryModel(Parcel in) {
        SupportCategoryId = in.readInt();
        Category = in.readString();
        CategoryTitle = in.readString();
        SupportRequestId = in.readString();
        UserName = in.readString();
        Description = in.readString();
        SupportPriority = in.readString();
        Status = in.readString();
        CreatedDateTime = in.readString();
    }

    public static final Creator<SupportCategoryModel> CREATOR = new Creator<SupportCategoryModel>() {
        @Override
        public SupportCategoryModel createFromParcel(Parcel in) {
            return new SupportCategoryModel(in);
        }

        @Override
        public SupportCategoryModel[] newArray(int size) {
            return new SupportCategoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(SupportCategoryId);
        dest.writeString(Category);
        dest.writeString(CategoryTitle);
        dest.writeString(SupportRequestId);
        dest.writeString(UserName);
        dest.writeString(Description);
        dest.writeString(SupportPriority);
        dest.writeString(Status);
        dest.writeString(CreatedDateTime);
    }
}
