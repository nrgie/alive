package com.blueobject.app.alive.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by nrgie on 2017.07.27..
 */

public class UserModel {

    public String fbid = "";

    public String name1 = "";
    public String name2 = "";
    public String name3 = "";
    public String email = "";
    public String national = "";
    public String bday = "";
    public double gender = 0;
    public String taj = "";

    public String avatar = "";

    public String phoneprefix = "";
    public String phoneprefixcode = "";
    public String phone = "";
    public String whatsapp = "";
    public String skype = "";
    public String facebook = "";
    public String snapchat = "";
    public String viber = "";

    public int blood = 0;
    public int bloodrh = 0;
    public String height = "";
    public String weight = "";
    public String allergy = "";

    public ArrayList<LangModel> langs = new ArrayList<LangModel>();

    public ArrayList<AllergyModel> allergies = new ArrayList<AllergyModel>();
    public ArrayList<AllergyModel> customallergies = new ArrayList<AllergyModel>();
    public ArrayList<MedModel> med = new ArrayList<MedModel>();
    public ArrayList<MedicalModel> medinfo = new ArrayList<MedicalModel>();
    public ArrayList<DoctorModel> doctors = new ArrayList<DoctorModel>();

    public String lat = "";
    public String lng = "";

    public String emcountry = "";
    public String emnumber = "";
    public String policenumber = "";
    public String firenumber = "";
    public String ambulancenumber = "";
    public String terrornumber = "";

    public ArrayList<PendingGuard> pending = new ArrayList<PendingGuard>();

    public ArrayList<UserModel> guards = new ArrayList<UserModel>();

    public ArrayList<ProtectedModel> protecteds = new ArrayList<ProtectedModel>();

    public String password = "";
    public boolean paid = false;

    public boolean soscall = true;
    public boolean sossms = true;
    public boolean gsoscall = true;
    public boolean gsossms = true;
    public boolean gsosemail = true;

    public boolean policecall = true;
    public boolean policesms = true;
    public boolean gpolicecall = true;
    public boolean gpolicesms = true;
    public boolean gpoliceemail = true;

    public boolean firecall = true;
    public boolean firesms = true;
    public boolean gfirecall = true;
    public boolean gfiresms = true;
    public boolean gfireemail = true;

    public boolean ambcall = true;
    public boolean ambsms = true;
    public boolean gambcall = true;
    public boolean gambsms = true;
    public boolean gambemail = true;

    public boolean tacall = true;
    public boolean tasms = true;
    public boolean gtacall = true;
    public boolean gtasms = true;
    public boolean gtaemail = true;

    public boolean cantrack = true;

    public boolean asGcall = true;
    public boolean asGsms = true;
    public boolean asGemail = true;
    public boolean asGfacebook = false;

    public String firebaseid = "";
    public int id = 0;
    //public String _id = "";
    public String name = "";
    public long avatarid = 0;
    public boolean invitesended = false;
    public boolean accepted = false;
    public boolean rejected = false;
    public boolean registered = false;
    public boolean datasetuped = false;

    public boolean enabled = true;

    public String deviceID = "";

    public String home = "";

    public String zip = "";
    public String city = "";
    public String street = "";
    public String streetno = "";
    public String floordoor = "";

    public void setValue(String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = this.getClass().getDeclaredField(fieldName);
        if (field.getType() == Character.TYPE) {field.set(this, value.charAt(0)); return;}
        if (field.getType() == Short.TYPE) {field.set(this, Short.parseShort(value)); return;}
        if (field.getType() == Integer.TYPE) {field.set(this, Integer.parseInt(value)); return;}
        if (field.getType() == Long.TYPE) {field.set(this, Long.parseLong(value)); return;}
        if (field.getType() == Float.TYPE) {field.set(this, Float.parseFloat(value)); return;}
        if (field.getType() == Double.TYPE) {field.set(this, Double.parseDouble(value)); return;}
        if (field.getType() == Byte.TYPE) {field.set(this, Byte.parseByte(value)); return;}
        if (field.getType() == Boolean.TYPE) {field.set(this, Boolean.parseBoolean(value)); return;}
        field.set(this, value);
    }

    public String getValue(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = this.getClass().getDeclaredField(fieldName);
        if(field == null) return "";
        return String.valueOf(field.get(this));
    }

}
