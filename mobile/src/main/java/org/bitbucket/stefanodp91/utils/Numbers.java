package org.bitbucket.stefanodp91.utils;

import android.content.Context;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Created by nrgie on 2017.07.30..
 */

public class Numbers {

    public Numbers() {}

    public CountryNum get(String code) throws JSONException {

        InputStream in = Global.appContext.getResources().openRawResource(R.raw.nums);

        CountryNum re = new CountryNum();

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String numbers = writer.toString();

        JSONObject data = new JSONObject(numbers);

        JSONArray array = data.getJSONArray("data");

        for (int i = 0 ; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);

            String iso  = obj.getString("iso");

            String A = obj.getString("A");
            String P = obj.getString("P");
            String F = obj.getString("F");
            String C = obj.getString("C");
            Boolean M = obj.getBoolean("M112");

            if(iso.equals(code)) {
                re.A = A;
                re.P = P;
                re.F = F;
                re.C = C;
                re.M112 = M;

                break;
            }
        }
        return re;
    }



}
