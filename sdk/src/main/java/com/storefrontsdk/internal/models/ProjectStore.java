package com.storefrontsdk.internal.models;

import java.io.Serializable;

public class ProjectStore implements Serializable {
    private Template template;

    private Integer id;

    private String name;

    private Type type;

    private Product[] products;

    private String total_products;

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public String getTotalProducts() {
        return total_products;
    }

    public void setTotalProducts(String total_products) {
        this.total_products = total_products;
    }
}
