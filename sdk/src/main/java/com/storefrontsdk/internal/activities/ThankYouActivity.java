package com.storefrontsdk.internal.activities;

import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

import com.storefrontsdk.R;
import com.storefrontsdk.ShopActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Kuba on 27/01/15.
 */
@EActivity(resName = "activity_thank_you")
public class ThankYouActivity extends BaseActivity {

    @ViewById(resName = "thank_you_message")
    protected TextView thankYouMessage;

    @AfterViews
    protected void start() {
        thankYouMessage.setText(
                getString(R.string.thank_you_message,
                        data.getProjectStore().getName(),
                        cartManager.getRecipient().email
                ));

        cartManager.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        setTitle(data.getProjectStore().getName());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    @Click(resName = "return_button")
    public void onBackPressed() {
        ShopActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
    }
}
