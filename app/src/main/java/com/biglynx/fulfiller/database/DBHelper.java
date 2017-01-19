package com.biglynx.fulfiller.database;

/**
 * Created by Biglynx on 8/1/2016.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.biglynx.fulfiller.models.RecentSearch;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RecentSearch.db";
    public static final String CONTACTS_TABLE_NAME = "search";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_PLACE = "place";
    public static final String CONTACTS_COLUMN_LAT = "lat";
    public static final String CONTACTS_COLUMN_LANG = "lang";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table search " +
                        "(id integer primary key, place text,lat text,lang text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS search");
        onCreate(db);
    }

    public boolean insertContact  (RecentSearch recentSearch)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("place", recentSearch.place);
        contentValues.put("lat", recentSearch.lat);
        contentValues.put("lang", recentSearch.lang);
        db.insert("search", null, contentValues);
        return true;
    }


    public ArrayList<RecentSearch> getAllSearchh()
    {
        ArrayList<RecentSearch> array_list = new ArrayList<RecentSearch>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from search ORDER BY id DESC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            RecentSearch recentSearch=new RecentSearch();
            recentSearch.id=res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID));
            recentSearch.place=res.getString(res.getColumnIndex(CONTACTS_COLUMN_PLACE));
            recentSearch.lat=res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_LAT));
            recentSearch.lang=res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_LANG));
            array_list.add(recentSearch);
            res.moveToNext();
        }
        return array_list;
    }
}
