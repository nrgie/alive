package com.blueobject.app.alive.helper;

/**
 * Created by nrgie on 2017.09.11..
 */

public class PendingGuard {
    public String name = "";
    public String email = "";

    public PendingGuard setName(String name) {
        this.name = name;
        return this;
    }

    public PendingGuard setEmail(String email) {
        this.email = email;
        return this;
    }

}