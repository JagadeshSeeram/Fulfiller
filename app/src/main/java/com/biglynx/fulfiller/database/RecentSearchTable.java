package com.biglynx.fulfiller.database;


import android.provider.BaseColumns;

public class RecentSearchTable {

    public static final String TABLE_NAME = "recent_searches";
    public static final String CREATE_TABLE = "";

    public static class Entry implements BaseColumns{
        public static final String PLACE_ID = "id";
        public static final String PLACE_NAME = "place";
        public static final String PLACE_LAT = "lat";
        public static final String PLACE_LANG = "lang";    }
}
