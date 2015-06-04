package com.storefrontsdk.internal.activities;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kubatatami.judonetworking.callbacks.ActivityCallback;
import com.github.kubatatami.judonetworking.observers.HolderView;
import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.storefrontsdk.Environment;
import com.storefrontsdk.R;
import com.storefrontsdk.internal.data.cart.CartRecipientModel;
import com.storefrontsdk.internal.models.CartProduct;
import com.storefrontsdk.internal.models.Options;
import com.storefrontsdk.internal.models.ShippingCost;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kuba on 01/02/15.
 */
@EActivity(resName = "activity_summary")
public class SummaryActivity extends BaseActivity {

    @Extra
    protected ShippingCost shippingCost;
    @ViewById(resName = "summary_list")
    protected ListView summaryList;

    static final int REQUEST_CODE_PAYMENT = 1;

    protected static class Holder {
        @HolderView(resName = "item_order_photo")
        ImageView orderImageView;
        @HolderView(resName = "item_order_description")
        TextView orderDescriptionView;
    }

    @AfterViews
    protected void start() {
        setTitle(getString(R.string.your_order));
        ObserverAdapter<CartProduct> productAdapter = new ObserverAdapter<CartProduct>(this,cartManager.getProducts(), R.layout.item_order) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent, Holder.class);
                Holder holder = (Holder) view.getTag();
                CartProduct product = getItem(position);
                String imageUrl=product.getComboImage().getMedium();
                ImageLoader.getInstance().displayImage(imageUrl, holder.orderImageView);
                String optionsText = "";
                for (Options options : product.getCombo().getOptions()) {
                    optionsText += options.getName() + " ";
                }
                String description = product.getProduct().getName()+"\n\n";
                description+=optionsText+"\n\n";
                description+="x"+product.getQuantity();

                holder.orderDescriptionView.setText(description);
                return view;
            }
        };

        String CONFIG_ENVIRONMENT = connection.getEnvironment().equals(Environment.PRODUCTION) ?
                PayPalConfiguration.ENVIRONMENT_PRODUCTION : PayPalConfiguration.ENVIRONMENT_SANDBOX;
        String CONFIG_CLIENT_ID = connection.getEnvironment().equals(Environment.PRODUCTION) ?
                "AUw5VBBDtG0DObsrS-lAvS6Y1vq_BJ6aZ6O82avw9i2oiE9QI_xDK3qNVbZY" : "Ad7hoBBMuIApCGxQlumX0JFIkIhLTVZgO26o5f7jUdVBDKPy02PcWFUMZY-J";


        PayPalConfiguration config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(CONFIG_CLIENT_ID)
                .merchantName("Awesome Shirts, Inc.")
                .merchantPrivacyPolicyUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/privacy-full"))
                .merchantUserAgreementUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/useragreement-full"));


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        summaryList.addHeaderView(SummaryHeader_.build(this, shippingCost, config));
        summaryList.setAdapter(productAdapter);
    }

    @OnActivityResult(REQUEST_CODE_PAYMENT)
    protected void paymentResult(int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            PaymentConfirmation confirm =
                    resultData.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    JSONObject responseObject = confirm.toJSONObject().getJSONObject("response");
                    savePurchase(responseObject.getString("id"), shippingCost.getTaxRate(), shippingCost.getAmount());
                    Log.i("paypal", confirm.toJSONObject().toString(4));
                    Log.i("paypal", confirm.getPayment().toJSONObject().toString(4));
                    ;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(
                    getApplicationContext(), R.string.transaction_was_canceled, Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void savePurchase(String transactionId, int tax, int shippingCost) {
        CartRecipientModel cartRecipientModel = cartManager.getRecipient();
        loadingDialog.show(getSupportFragmentManager(), "loading");
        loadingDialog.setCancelable(false);
        connection.getApi().savePurchase(
                cartManager.getCurrency(),
                data.getProjectStore().getId(),
                Math.round(cartManager.getProductsTotalCost() * 100), transactionId, tax, shippingCost,
                cartRecipientModel.recipientName, cartRecipientModel.email,
                cartRecipientModel.shippingAddress, cartRecipientModel.city, cartRecipientModel.zipCode,
                cartRecipientModel.countryCode, cartRecipientModel.getProvinceOrState(), cartManager.getItemCount(),
                data.getPayPalLaunches(), cartManager.getProductsMap(), new ActivityCallback<ShippingCost>(this) {
                    @Override
                    public void onSafeSuccess(ShippingCost result) {
                        if (loadingDialog.isAdded()) {
                            ThankYouActivity_.intent(SummaryActivity.this).start();
                        }
                    }

                    @Override
                    public void onSafeFinish() {
                        loadingDialog.dismiss();
                    }
                });
        data.setPayPalLaunches(0);

    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}
