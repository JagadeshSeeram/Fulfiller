package com.biglynx.fulfiller.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/28/2016.
 */
public class BroadCast implements Serializable {
    public String id;
    public String lat;
    public String lang;
    public String name;
    public String noOfFullmets;
    public String RetailerId;
    public String BusinessLegalName;
    public String CompanyEmail;
    public String Phone;
    public String CompanyLogo;
    public List<FulfillersDTO> Fulfillments;
    public RetailerLocationAddress RetailerLocationAddress;
    public FulfillerInterests FulfillerInterests;
    public boolean isExpressed;


    public BroadCast(String ids,String s, String s1, String test1, String s2) {
        this.lat=s;
        this.id=ids;
        this.lang=s1;
        this.name=test1;
        this.noOfFullmets=s2;
    }
}
