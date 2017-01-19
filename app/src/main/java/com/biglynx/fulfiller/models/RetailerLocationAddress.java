package com.biglynx.fulfiller.models;

import java.io.Serializable;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class RetailerLocationAddress implements Serializable{

    public String AddressLine1;
    public String AddressLine2;
    public String City;
    public String State;
    public String Zip;
    public String CountryName;
    public String Latitude;
    public String Longitude;
    public String Degrees;
    public String Minutes;
    public String Seconds;
    public String LocationType;
    public String RetailerLocationId;
    public RetailerLocationAddress RetailerLocationAddress;

}
