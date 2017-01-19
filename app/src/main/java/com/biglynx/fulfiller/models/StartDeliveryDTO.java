package com.biglynx.fulfiller.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/22/2016.
 */

public class StartDeliveryDTO implements Serializable{

    public int FulfillerId;
    public String  Status;
    public String ExpirationDateTime;
    public int OrderCount;
    public String TotalDistance;
    public int FulfillmentId;
    public String PriceType;
    public String  Amount;
    public String  ExpirationDateTime1;
    public String  DueDate;
    public DeliveryPerson DeliveryPerson;
    public RetailerLocation RetailerLocation;
    public List<Orders> Orders;
    public List<DeliveryStatus> DeliveryStatus;
}
