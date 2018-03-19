package com.blueobject.app.alive.helper;

import android.text.format.Time;

/**
 * Created by nrgie on 2017.07.30..
 */

public class MedModel {
    public String name = "";
    public String qty = "";
    public String pic = "";

    public String getFileName() {
        if(pic.equals("")) {
            Time t = new Time();
            t.setToNow();
            int timeFileMinute = t.minute;
            int timeFileDate = t.yearDay;
            int timeFileYear = t.year;
            long timestamp = System.currentTimeMillis();
            pic = "Medicine-" + timeFileMinute + timeFileDate + timeFileYear + android.os.Build.SERIAL;
        }

        return pic;
    }

}

