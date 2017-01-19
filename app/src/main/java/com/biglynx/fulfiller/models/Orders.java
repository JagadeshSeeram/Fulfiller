package com.biglynx.fulfiller.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Biglynx on 7/26/2016.
 */
public class Orders implements Serializable{

    public OrderAddress OrderAddress;
    public String OrderId;
    public String CustomerFirstName;
    public String CustomerLastName;
    public String CustomerPhone;
    public String NoOfPackages;
    public String PackageInfo;
    public String PackageSize;
    public String PackageWeight;
    public int DeliveryStatusId;
    public String Status;
}
