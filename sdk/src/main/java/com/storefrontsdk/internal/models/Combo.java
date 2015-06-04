package com.storefrontsdk.internal.models;

import java.io.Serializable;
import java.util.Map;

public class Combo implements Serializable {
    private String id;

    private String name;

    private ProductType product_type;

    private Options[] options;

    private Map<String, Double> price_modifier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getProductType() {
        return product_type;
    }

    public void setProductType(ProductType product_type) {
        this.product_type = product_type;
    }

    public Options[] getOptions() {
        return options;
    }

    public void setOptions(Options[] options) {
        this.options = options;
    }

    public Map<String, Double> getPriceModifier() {
        return price_modifier;
    }

    public void setPriceModifier(Map<String, Double> price_modifier) {
        this.price_modifier = price_modifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Combo combo = (Combo) o;

        return id.equals(combo.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}