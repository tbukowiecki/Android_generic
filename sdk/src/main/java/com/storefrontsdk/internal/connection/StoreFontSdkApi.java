package com.storefrontsdk.internal.connection;

import com.github.kubatatami.judonetworking.AsyncResult;
import com.github.kubatatami.judonetworking.annotations.LocalCache;
import com.github.kubatatami.judonetworking.annotations.RequestMethod;
import com.github.kubatatami.judonetworking.callbacks.Callback;
import com.github.kubatatami.judonetworking.controllers.raw.RawRestController;
import com.storefrontsdk.internal.models.ProjectStore;
import com.storefrontsdk.internal.models.ShippingCost;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by Kuba on 24/01/15.
 */
public interface StoreFontSdkApi {

    public final static int GET_VIEW_PROJECT_STORE_ID = 1;

    @RawRestController.Rest("?cmd=ViewProjectStore")
    @RequestMethod(async = true, id = GET_VIEW_PROJECT_STORE_ID)
    @LocalCache()
    @RawRestController.FormPost
    public AsyncResult getViewProjectStore(
            Callback<ProjectStore> callback);

    @RawRestController.Rest("?cmd=SavePurchase")
    @RequestMethod(async = true, timeout = 1000 * 40)
    @RawRestController.FormPost
    public AsyncResult savePurchase(
            @RawRestController.Post("currency") String currency,
            @RawRestController.Post("store_id") int storeId,
            @RawRestController.Post("total") long total,
            @RawRestController.Post("transaction_id") String transactionId,
            @RawRestController.Post("tax") int tax,
            @RawRestController.Post("shipping_cost") int shippingCost,
            @RawRestController.Post("shipping_recipient") String shipping_recipient,
            @RawRestController.Post("email") String email,
            @RawRestController.Post("shipping_address1") String shipping_address1,
            @RawRestController.Post("shipping_city") String shipping_city,
            @RawRestController.Post("shipping_postal_code") String shipping_postal_code,
            @RawRestController.Post("shipping_country") String shipping_country,
            @RawRestController.Post("shipping_province") String shipping_province,
            @RawRestController.Post("items_in_cart") int items_in_cart,
            @RawRestController.Post("paypal_launches") int paypal_launches,
            @RawRestController.AdditionalPostParam List<NameValuePair> purchaseItems,
            Callback<ShippingCost> callback);


    @RawRestController.Rest("?cmd=GetShippingCost")
    @RequestMethod(async = true)
    @RawRestController.FormPost
    public AsyncResult getShippingCost(
            @RawRestController.Post("currency") String currency,
            @RawRestController.Post("store_id") int storeId,
            @RawRestController.Post("total") long total,
            @RawRestController.Post("transaction_id") String transactionId,
            @RawRestController.Post("tax") String tax,
            @RawRestController.Post("shipping_cost") String shippingCost,
            @RawRestController.Post("shipping_recipient") String shipping_recipient,
            @RawRestController.Post("email") String email,
            @RawRestController.Post("shipping_address1") String shipping_address1,
            @RawRestController.Post("shipping_city") String shipping_city,
            @RawRestController.Post("shipping_postal_code") String shipping_postal_code,
            @RawRestController.Post("shipping_country") String shipping_country,
            @RawRestController.Post("shipping_province") String shipping_province,
            @RawRestController.Post("items_in_cart") int items_in_cart,
            @RawRestController.Post("paypal_launches") int paypal_launches,
            @RawRestController.AdditionalPostParam List<NameValuePair> purchaseItems,
            Callback<ShippingCost> callback);


}
