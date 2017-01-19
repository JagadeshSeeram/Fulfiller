package com.biglynx.fulfiller.models;

import java.io.Serializable;

/**
 * Copyright (c) 2016 BigLynx
 * <p>
 * All Rights reserved.
 */

/*
* @author Ramakrishna on 8/18/2016
* @version 1.0
*
*/

public class FulfillerInterests implements Serializable {
    public String FulfillerInterestId;
    public String FulfillmentId;
    public String InterestStatus;
    public String FulfillerType;
    public String PriceType;
    public double Amount;
    public String InterestDateTime;
    public String InvoiceID;
    public String CancelledDatetime;
    public String DateUpdated;
    public  String ConfirmationCode;
    public  String FulfillerId;
    public String InterestExpirationDateTime;
}
