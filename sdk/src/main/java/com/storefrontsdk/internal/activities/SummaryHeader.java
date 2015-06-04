package com.storefrontsdk.internal.activities;

import android.content.Intent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.storefrontsdk.R;
import com.storefrontsdk.internal.data.DataContainer;
import com.storefrontsdk.internal.data.cart.CartManager;
import com.storefrontsdk.internal.models.ShippingCost;
import com.storefrontsdk.internal.utils.CurrencyUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;

/**
 * Created by Kuba on 26/01/15.
 */
@EViewGroup(resName = "header_summary")
public class SummaryHeader extends FrameLayout {

    @Bean
    protected DataContainer data;
    @Bean
    protected CartManager cartManager;


    @ViewById(resName = "subtotal")
    protected TextView subtotalView;
    @ViewById(resName = "shipping")
    protected TextView shippingView;
    @ViewById(resName = "taxRate")
    protected TextView taxRateView;
    @ViewById(resName = "order_total")
    protected TextView orderTotalView;
    @ViewById(resName = "tax_rate_percent")
    protected TextView taxRatePercentView;

    @ViewById(resName = "order_edit")
    protected TextView orderEditView;
    @ViewById(resName = "shipping_edit")
    protected TextView shippingEditView;

    @ViewById(resName = "recp_info")
    protected TextView recpInfoView;

    protected SummaryActivity activity;
    protected PayPalConfiguration config;
    protected ShippingCost shippingCost;

    protected String currency;
    protected double value;

    protected SummaryHeader(SummaryActivity activity, ShippingCost shippingCost, PayPalConfiguration config) {
        super(activity);
        this.config = config;
        this.activity = activity;
        this.shippingCost = shippingCost;

    }

    @AfterViews
    protected void start() {
        double shippingCostAmount = (double) shippingCost.getAmount() / 100d;
        double textRate = (double) shippingCost.getTaxRate() / 100d;
        value = shippingCostAmount + textRate + cartManager.getProductsTotalCost();
        subtotalView.setText(CurrencyUtils.getCurrencyValue(shippingCost.getCurrency(), cartManager.getProductsTotalCost()));
        shippingView.setText(CurrencyUtils.getCurrencyValue(shippingCost.getCurrency(), shippingCostAmount));
        taxRateView.setText(CurrencyUtils.getCurrencyValue(shippingCost.getCurrency(), textRate));
        orderTotalView.setText(CurrencyUtils.getCurrencyValue(shippingCost.getCurrency(), value));

        taxRatePercentView.setText(activity.getString(R.string.tax_rate, textRate / cartManager.getProductsTotalCost() + "%"));

        orderEditView.setBackgroundColor(data.getProjectStore().getTemplate().getActionBarBgColor());
        shippingEditView.setBackgroundColor(data.getProjectStore().getTemplate().getActionBarBgColor());
        recpInfoView.setText(
                cartManager.getRecipient().recipientName + "\n" +
                        cartManager.getRecipient().shippingAddress + "\n" +
                        cartManager.getRecipient().shippingAddressSecondLine + "\n" +
                        cartManager.getRecipient().city + ", " + cartManager.getRecipient().getProvinceOrState() + ", " + cartManager.getRecipient().zipCode + "\n" +
                        cartManager.getRecipient().countryCode + "\n"
        );

    }

    @Click(resName = "order_edit")
    protected void orderEditClick() {
        CartActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).start();

    }

    @Click(resName = "shipping_edit")
    protected void shippingEditClick() {
        FormActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
    }


    @Click(resName = "paypal_buy")
    protected void payPalBuy() {
        data.setPayPalLaunches(data.getPayPalLaunches() + 1);
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        activity.startActivityForResult(intent, SummaryActivity.REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        currency = CurrencyUtils.getCurrency(cartManager.getProducts().get(0).getCombo().getPriceModifier());
        return new PayPalPayment(new BigDecimal(value), currency, data.getProjectStore().getName(),
                paymentIntent);
    }

}
