package com.biglynx.fulfiller.models;

import java.io.Serializable;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class RetailerLocation implements Serializable{
    public RetailerLocationAddress RetailerLocationAddress;
    public Retailer Retailer;
    public String LocationContactPerson;
    public String RetailerLocationId;
    public String LocationType;
    public String LocatedInMall;
    public String LocationContactRole;
    public String LocationContactPhone;
    public String LocationContactMail;
}
