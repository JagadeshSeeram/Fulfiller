package com.biglynx.fulfiller.network;

public class NetworkResponse {
    private Object responseObject;
    private String responseStatus;
    private String responseString;
    private int statusCode;
    private Object tag;

    public Object getResponseObject() {
        return this.responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statuscode) {
        this.statusCode = statuscode;
    }

    public String getResponseString() {
        return this.responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public String getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
