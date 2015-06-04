package com.storefrontsdk.internal.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kubatatami.judonetworking.callbacks.ActivityCallback;
import com.storefrontsdk.R;
import com.storefrontsdk.internal.data.cart.CartRecipientModel;
import com.storefrontsdk.internal.models.ShippingCost;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Kuba, Jacek
 * Date: 26/01/15
 * Time: 23:39
 */
@EActivity(resName = "activity_form")
public class FormActivity extends BaseActivity {

    private static final int CHOOSE_COUNTRY = 0;
    private static final int CHOOSE_STATE = 1;
    private static final int CHOOSE_PROVINCE = 2;

    @StringArrayRes(resName = "countries")
    String[] countries;
    @StringArrayRes(resName = "states")
    String[] states;
    @StringArrayRes(resName = "states_short")
    String[] statesShort;
    @StringArrayRes(resName = "provinces")
    String[] provinces;

    @ViewById(resName = "form_recipient_name")
    EditText recipientName;
    @ViewById(resName = "form_shipping_street_address")
    EditText shippingAddress;
    @ViewById(resName = "form_addres_2_line")
    EditText shippingAddressSecond;
    @ViewById(resName = "form_city")
    EditText city;
    @ViewById(resName = "form_zip_code")
    EditText zipCode;
    @ViewById(resName = "form_country")
    TextView country;
    @ViewById(resName = "form_state_province")
    TextView stateProvince;
    @ViewById(resName = "form_email")
    EditText email;

    private CartRecipientModel cartRecipientModel;

    @AfterViews
    protected void start() {
        setTitle(getString(R.string.shipping_to));

        cartManager.loadRecipientModel();
        cartRecipientModel = cartManager.getRecipient();

        fillForm();
    }

    @Click(resName = "continue_button")
    protected void continueClick() {
        gatherInput(cartRecipientModel);

        String[] validateErrors = cartRecipientModel.validateForm(this, countries);
        List<String> badFields = filterNonNullValues(validateErrors);
        boolean isValid = badFields.size() == 0;

        if (isValid) {
            sendGetShippingCost();
        } else {
            String badFieldsString = TextUtils.join(", ", badFields);
            Toast.makeText(this, getString(com.storefrontsdk.R.string.form_validation_error)
                    + badFieldsString, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendGetShippingCost() {
        loadingDialog.show(getSupportFragmentManager(), "loading");
        connection.getApi().getShippingCost(
                cartManager.getCurrency(),
                data.getProjectStore().getId(),
                Math.round(cartManager.getProductsTotalCost() * 100), "", "", "",
                cartRecipientModel.recipientName, cartRecipientModel.email,
                cartRecipientModel.shippingAddress, cartRecipientModel.city, cartRecipientModel.zipCode,
                cartRecipientModel.countryCode, cartRecipientModel.getProvinceOrState(), cartManager.getItemCount(),
                0, cartManager.getProductsMap(), new ActivityCallback<ShippingCost>(this) {
                    @Override
                    public void onSafeSuccess(ShippingCost result) {
                        if (loadingDialog.isAdded()) {
                            SummaryActivity_.intent(FormActivity.this).shippingCost(result).start();
                        }
                    }

                    @Override
                    public void onSafeFinish() {
                        loadingDialog.dismiss();
                    }
                });
    }

    @Click(resName = "form_country")
    void onCountryClick() {
        showDialog(CHOOSE_COUNTRY, getString(com.storefrontsdk.R.string.form_choose_country), countries);
    }

    @Click(resName = "form_state_province")
    void onStateProvinceClick() {
        if (!TextUtils.isEmpty(cartRecipientModel.countryCode)
                && cartRecipientModel.countryCode.equalsIgnoreCase(countries[1])) { //CA
            showDialog(CHOOSE_PROVINCE, getString(com.storefrontsdk.R.string.form_choose_province), provinces);
        } else {
            showDialog(CHOOSE_STATE, getString(com.storefrontsdk.R.string.form_choose_state), states);
        }
    }

    @Override
    protected void onPause() {
        gatherInput(cartRecipientModel);
        cartManager.storeRecipientModel();
        super.onPause();
    }

    private List<String> filterNonNullValues(String[] validateErrors) {
        List<String> result = new ArrayList<>();
        for (String string : validateErrors) {
            if (!TextUtils.isEmpty(string)) {
                result.add(string);
            }
        }
        return result;
    }

    private void gatherInput(CartRecipientModel cartRecipientModel) {
        cartRecipientModel.recipientName = recipientName.getText().toString();
        cartRecipientModel.shippingAddress = shippingAddress.getText().toString();
        cartRecipientModel.shippingAddressSecondLine = shippingAddressSecond.getText().toString();
        cartRecipientModel.city = city.getText().toString();
        cartRecipientModel.zipCode = zipCode.getText().toString();
        cartRecipientModel.email = email.getText().toString();
    }

    private void fillForm() {
        recipientName.setText(cartRecipientModel.recipientName);
        shippingAddress.setText(cartRecipientModel.shippingAddress);
        shippingAddressSecond.setText(cartRecipientModel.shippingAddressSecondLine);
        city.setText(cartRecipientModel.city);
        zipCode.setText(cartRecipientModel.zipCode);
        email.setText(cartRecipientModel.email);

        if (!TextUtils.isEmpty(cartRecipientModel.countryCode)) {
            if (!TextUtils.isEmpty(cartRecipientModel.state)) {
                stateProvince.setText(cartRecipientModel.state);
            } else if (!TextUtils.isEmpty(cartRecipientModel.province)) {
                stateProvince.setText(cartRecipientModel.province);
            } else {
                if (cartRecipientModel.countryCode.equalsIgnoreCase(countries[0])) { // US
                    cartRecipientModel.state = states[0];
                    stateProvince.setText(cartRecipientModel.state);
                } else {
                    cartRecipientModel.province = provinces[0];
                    stateProvince.setText(cartRecipientModel.province);
                }
            }
        } else {
            cartRecipientModel.countryCode = countries[0];
            cartRecipientModel.state = states[0];
            stateProvince.setText(cartRecipientModel.state);
        }

        country.setText(cartRecipientModel.countryCode);
    }

    private void showDialog(final int chooseType, String title, final String[] items) {
        gatherInput(cartRecipientModel);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(title);
        builderSingle.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String clickedItem = items[which];
                boolean shouldRefillForm = false;

                switch (chooseType) {
                    case CHOOSE_COUNTRY:
                        shouldRefillForm = TextUtils.isEmpty(cartRecipientModel.countryCode)
                                || !cartRecipientModel.countryCode.equalsIgnoreCase(clickedItem);
                        if (shouldRefillForm) {
                            cartRecipientModel.countryCode = clickedItem;
                            cartRecipientModel.state = null;
                            cartRecipientModel.province = null;
                        }
                        break;
                    case CHOOSE_STATE:
                        shouldRefillForm = TextUtils.isEmpty(cartRecipientModel.state)
                                || !cartRecipientModel.state.equalsIgnoreCase(clickedItem);
                        if (shouldRefillForm) {
                            cartRecipientModel.state = clickedItem;
                            cartRecipientModel.province = null;
                        }
                        break;
                    case CHOOSE_PROVINCE:
                        shouldRefillForm = TextUtils.isEmpty(cartRecipientModel.province)
                                || !cartRecipientModel.province.equalsIgnoreCase(clickedItem);
                        if (shouldRefillForm) {
                            cartRecipientModel.state = null;
                            cartRecipientModel.province = clickedItem;
                        }
                        break;
                }

                if (shouldRefillForm) {
                    fillForm();
                }
            }
        });

        builderSingle.setNegativeButton(getString(com.storefrontsdk.R.string.form_choose_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }
}
