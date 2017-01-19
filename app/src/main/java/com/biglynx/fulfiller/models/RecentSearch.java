package com.biglynx.fulfiller.models;

/**
 * Created by Biglynx on 8/1/2016.
 */
public class RecentSearch {
    public String id;;
    public String place;
    public double lat;
    public double lang;


    public RecentSearch(String ids, String address, double latitude, double longitude) {
        this.id=ids;
        this.place=address;
        this.lat=latitude;
        this.lang=longitude;
    }

    public RecentSearch() {

    }
}
