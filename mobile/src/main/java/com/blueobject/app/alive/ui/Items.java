package com.blueobject.app.alive.ui;


public class Items {

    private String title;
    private String item_new;
    private int icon;

    public Items() {

    }

    public Items(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public Items(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getItem_new() {
        return item_new;
    }

    public void setItem_new(String item_new) {
        this.item_new = item_new;
    }
}