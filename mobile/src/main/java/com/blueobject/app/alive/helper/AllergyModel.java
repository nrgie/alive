package com.blueobject.app.alive.helper;

/**
 * Created by nrgie on 2017.09.11..
 */

public class AllergyModel {
    public String name = "";

    public String hu = "";
    public String en = "";
    public String de = "";
    public String key = "";

    public boolean checked = false;

    public AllergyModel setName(String name){
        this.name = name;
        return this;
    }

    public AllergyModel check(boolean c){
        this.checked = c;
        return this;
    }

}