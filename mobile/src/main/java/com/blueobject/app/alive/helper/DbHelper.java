package com.blueobject.app.alive.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blueobject.app.alive.dbpreferences.DatabaseBasedSharedPreferences;

import java.io.File;

/**
 * Created by nrgie on 2016.08.26..
 */

public class DbHelper extends SQLiteOpenHelper {

    private final Context m_ctx;
    private static final String DATABASE_NAME    = "APP_DB";
    private static final int    DATABASE_VERSION = 16110400;
    //public static SharedPreferences shared;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        m_ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE IF NOT EXISTS ROUTES(_id INTEGER PRIMARY KEY,userid INT,routename VARCHAR, renamed BOOLEAN,geocoded BOOLEAN, datum DATE DEFAULT (datetime('now','localtime')),isClosed BOOLEAN,distance FLOAT,ftime INT,ptime INT,etime INT,maxspeed FLOAT,avgspeed FLOAT,img1 TEXT,img2 TEXT);");
        //db.execSQL("CREATE TABLE IF NOT EXISTS ROUTEPOINTS(_id INTEGER PRIMARY KEY,routeid INT,datum DATE DEFAULT (datetime('now','localtime')),lat DOUBLE,lon DOUBLE,altitude DOUBLE,speed FLOAT,status INT, newpoly BOOLEAN);");
        //db.execSQL("CREATE TABLE IF NOT EXISTS ROUTEMARKERS(_id INTEGER PRIMARY KEY,routeid INT,datum DATE DEFAULT (datetime('now','localtime')),lat DOUBLE,lon DOUBLE,type TEXT,photo TEXT,address TEXT,adr_city TEXT,pausetime INT,poi TEXT);");
        //db.execSQL("CREATE TABLE IF NOT EXISTS ROUTELOG(_id INTEGER PRIMARY KEY,routeid INT,datum DATE DEFAULT (datetime('now','localtime')),log TEXT);");
        //db.execSQL("CREATE TABLE IF NOT EXISTS TRACKERLOG(_id INTEGER PRIMARY KEY,routeid INT,datum DATE DEFAULT (datetime('now','localtime')),log TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<16110400){
            //db.execSQL("DROP TABLE IF EXISTS ROUTES");
            //db.execSQL("DROP TABLE IF EXISTS ROUTEPOINTS");
            //db.execSQL("DROP TABLE IF EXISTS ROUTEMARKERS");
            //db.execSQL("DROP TABLE IF EXISTS ROUTELOG");
            //db.execSQL("DROP TABLE IF EXISTS TRACKERLOG");
			
			File dir=m_ctx.getApplicationContext().getCacheDir();
			//Global.deleteDir(dir);
			//SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(m_ctx);
            DatabaseBasedSharedPreferences shared = new DatabaseBasedSharedPreferences(m_ctx);
			shared.edit().clear().commit();
			
			onCreate(db);
        }else{
            if(oldVersion<16092100){
                //db.execSQL("ALTER TABLE ROUTES ADD COLUMN geocoded BOOLEAN");
            }
		}		
    }

    public static int insertroute(Context ctx, String name){
        boolean result = false;
        DbHelper dbh = null;
        SQLiteDatabase db = null;
        int id = 0;
        try {
            dbh = new DbHelper(ctx);
            db = dbh.getWritableDatabase();

            ContentValues values = new ContentValues();
            //values.put("userid", MainActivity.User.id);
            values.put("routename", name);
            values.put("isClosed", 0);

            id = (int) db.insert("ROUTES", null, values);

        } catch (Throwable e) {
            Log.e("Houston", "Db access problem 0");
        } finally {
            if (null != db)
                db.close();
            if (null != dbh)
                dbh.close();
        }
        return id;
    }


    public static int log(Context ctx, int routeid,String log){
        boolean result = false;
        DbHelper dbh = null;
        SQLiteDatabase db = null;
        int id = 0;
        try {
            dbh = new DbHelper(ctx);
            db = dbh.getWritableDatabase();
            ContentValues v=new ContentValues();
            v.put("routeid",routeid);
            v.put("log",log);

            id = (int) db.insert("ROUTELOG", null, v);
        } catch (Throwable e) {
            Log.e("Houston", "Db access problem log");
        } finally {
            if (null != db)
                db.close();
            if (null != dbh)
                dbh.close();
        }
        return id;
    }




}
