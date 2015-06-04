package com.storefrontsdk.internal.models;

import android.graphics.Color;

import java.io.Serializable;

public class Template implements Serializable {
    private String bg_color2;

    private String id;

    private String bg_color1;

    private String bg_color4;

    private String bg_color3;

    private String bg_color;

    private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getActionBarBgColor() {
        return Color.parseColor("#" + bg_color);
    }

    public int getProductBgColor() {
        return Color.parseColor("#" + bg_color1);
    }

    public int getDescriptionBgColor() {
        return Color.parseColor("#" + bg_color2);
    }

    public int getActionBarTextColor() {
        return Color.parseColor("#" + bg_color3);
    }

    public int getDescriptionTextColor() {
        return Color.parseColor("#" + bg_color4);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}