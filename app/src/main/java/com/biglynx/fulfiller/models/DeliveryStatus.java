package com.biglynx.fulfiller.models;

import java.io.Serializable;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class DeliveryStatus implements Serializable{
    public String FulfillmentId;
    public String FulfillerId;
    public String FriendlyName;
    public String DeliveryStatus;
    public String DeliveryStatusId;
    public String ProgressMessage;
    public String GeoLocationLatitude;
    public String GeoLocationLongitude;
    public String DateCreated;


}
