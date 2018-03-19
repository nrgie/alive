package com.blueobject.app.alive.helper;

/**
 * Created by nrgie on 2017.09.11..
 */

public class LangModel {
    public String name = "";
    public String slug = "";
    public boolean checked = false;

    public LangModel setName(String name){
        this.name = name;
        return this;
    }

    public LangModel check(boolean c){
        this.checked = c;
        return this;
    }

}