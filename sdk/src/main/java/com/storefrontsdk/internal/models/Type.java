package com.storefrontsdk.internal.models;

import java.io.Serializable;
import java.util.Map;

public class Type implements Serializable {
    private String id;

    private Map<String, Integer> price;

    private String description;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getPrice() {
        return price;
    }

    public void setPrice(Map<String, Integer> price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}