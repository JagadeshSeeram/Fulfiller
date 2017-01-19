package com.biglynx.fulfiller.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class FulfillersDTO implements Serializable {

    public String FulfillerId;
    public String Status;
    public String RetailerName;
    public String OrderCount;
    public double TotalDistance;
    public String FulfillmentId;
    public String PriceType;
    public double Amount;
    public String BusinessLegalName;

    public String ExpirationDateTime1;
    public String DueDate;

    public double PickUpMapLatitude;
    public double PickUpMapLongitude;
    public double MinDistance;
    public double MaxDistance;
    public double TotalWeight;
    public String TotalPackages;
    public String LocationContactPerson;
    public String LocationContactRole;
    public String LocationContactPhone;
    public String LocationContactMail;
    public String DateUpdated;
    public String TotalApproxTimeInSeconds;
    public String ExpirationDateTime;
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
}
