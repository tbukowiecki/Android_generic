package com.storefrontsdk.internal.models;

import java.io.Serializable;

/**
 * Created by Kuba on 29/01/15.
 */
public class ShippingCost implements Serializable {
    private String currency;
    private Integer amount;
    private Integer tax_rate;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTaxRate() {
        return tax_rate;
    }

    public void setTaxRate(Integer taxRate) {
        this.tax_rate = taxRate;
    }
}
