package com.storefrontsdk.internal.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kuba on 24/01/15.
 */
public class CurrencyUtils {


    public static String getCurrencyValue(String currency, double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(currency));
        return format.format(value);
    }

    public static String getCurrencyValue(Map<String, Double> currencyValueMap) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(getCurrency(currencyValueMap)));
        return format.format(getValue(currencyValueMap));
    }

    public static String getCurrency(Map<String, Double> currencyValueMap) {
        String currency;
        String defaultCurrency = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        if (currencyValueMap.containsKey(defaultCurrency)) {
            currency = defaultCurrency;
        } else {
            List<String> keyList = new ArrayList<>(currencyValueMap.keySet());
            currency = keyList.get(0);
        }
        return currency;
    }

    public static double getValue(Map<String, Double> currencyValueMap) {
        double value;
        String defaultCurrency = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        if (currencyValueMap.containsKey(defaultCurrency)) {
            value = currencyValueMap.get(defaultCurrency);
        } else {
            List<Double> valueList = new ArrayList<>(currencyValueMap.values());
            value = valueList.get(0);
        }
        return value;
    }

}
