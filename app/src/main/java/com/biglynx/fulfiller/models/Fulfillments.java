package com.biglynx.fulfiller.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class Fulfillments implements Serializable{

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


}
