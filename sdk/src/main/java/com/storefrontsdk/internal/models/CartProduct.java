package com.storefrontsdk.internal.models;

import com.storefrontsdk.internal.utils.CurrencyUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Kuba on 29/01/15.
 */
public class CartProduct implements Serializable {

    protected Product product;
    protected int quantity;
    protected Combo combo;

    public CartProduct(Product product, int quantity, Combo combo) {
        this.product = product;
        this.quantity = quantity;
        this.combo = combo;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Combo getCombo() {
        return combo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartProduct that = (CartProduct) o;

        return combo.equals(that.combo) && product.equals(that.product);

    }

    @Override
    public int hashCode() {
        int result = product.hashCode();
        result = 31 * result + combo.hashCode();
        return result;
    }

    public double getPriceTotal() {
        return (double) quantity * CurrencyUtils.getValue(getCombo().getPriceModifier());
    }

    public Image getComboImage() {
        Image[] images = getProduct().getImage();
        Map<String, Image> comboMap = images[images.length-1].getComboImage();
        if(comboMap!=null) {
            return comboMap.get(getCombo().getId());
        }else{
            return images[0];
        }
    }
}
