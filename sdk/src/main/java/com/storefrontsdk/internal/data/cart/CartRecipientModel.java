package com.storefrontsdk.internal.data.cart;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * User: jacek
 * Date: 27/01/15
 * Time: 23:28
 * modrakowski.pl/android
 */
public class CartRecipientModel implements Serializable {

    public String recipientName;
    public String shippingAddress;
    public String shippingAddressSecondLine;
    public String city;
    public String zipCode;
    public String countryCode;
    public String state;
    public String province;
    public String email;

    public String[] validateForm(Context context, String[] countries) {
        return new String[]{
                validateField(city, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_city)),
                validateField(countryCode, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_country_code)),
                validateStateProvince(context, countries),
                validateField(recipientName, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_recipient_name)),
                validateField(shippingAddress, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_shipping_address)),
                validateEmailField(email, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_email)),
        };
    }

    private String validateField(String value, String errorMessage) {
        return TextUtils.isEmpty(value) ? errorMessage : null;
    }

    private String validateEmailField(String email, String errorMessage) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                ? null : errorMessage;
    }

    private String validateStateProvince(Context context, String[] countries) {
        String result = null;
        if (!TextUtils.isEmpty(countryCode)) {
            if (countryCode.equalsIgnoreCase(countries[0])) {  //US
                result = validateField(state, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_state));
            } else {
                result = validateField(province, context.getString(com.storefrontsdk.R.string.recipient_model_invalid_province));
            }
        }
        return result;
    }

    public String getProvinceOrState() {
        return province != null ? province : state;
    }
}
