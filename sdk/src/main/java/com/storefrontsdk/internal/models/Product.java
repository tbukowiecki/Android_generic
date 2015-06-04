package com.storefrontsdk.internal.models;

import android.text.Html;

import java.io.Serializable;
import java.util.Map;

public class Product implements Serializable {
    private Integer id;

    private String description;

    private String name;

    private Image[] image;

    private Combo[] combo;

    private Map<String, Double> commission;

    private Type type;

    private String default_combo;

    private int quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return Html.fromHtml(description).toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return Html.fromHtml(name).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image[] getImage() {
        return image;
    }

    public void setImage(Image[] image) {
        this.image = image;
    }

    public Combo[] getCombo() {
        return combo;
    }

    public void setCombo(Combo[] combo) {
        this.combo = combo;
    }

    public Map<String, Double> getCommission() {
        return commission;
    }

    public void setCommission(Map<String, Double> commission) {
        this.commission = commission;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Combo getDefaultCombo() {
        for (Combo comb : combo) {
            if (comb.getId().equals(default_combo)) {
                return comb;
            }
        }
        return null;
    }
}

			