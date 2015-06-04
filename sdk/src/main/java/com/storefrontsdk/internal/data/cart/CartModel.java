package com.storefrontsdk.internal.data.cart;

import com.storefrontsdk.internal.models.CartProduct;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User: jacek
 * Date: 27/01/15
 * Time: 23:25
 */
public class CartModel implements Serializable {
    ArrayList<CartProduct> products = new ArrayList<>();
}
