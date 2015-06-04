package com.storefrontsdk.internal.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.storefrontsdk.R;
import com.storefrontsdk.internal.connection.Connection;
import com.storefrontsdk.internal.data.DataContainer;
import com.storefrontsdk.internal.data.cart.CartManager;
import com.storefrontsdk.internal.dialogs.LoadingDialogFragment_;
import com.storefrontsdk.internal.models.ProjectStore;
import com.storefrontsdk.internal.utils.LayoutTraverser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.res.DrawableRes;

/**
 * Created by Kuba on 25/01/15.
 */
@EActivity
@OptionsMenu(resName = "menu_shop")
public class BaseActivity extends ActionBarActivity {

    @Bean
    protected DataContainer data;
    @Bean
    protected CartManager cartManager;
    @Bean
    protected Connection connection;

    @DrawableRes(resName = "abc_ic_ab_back_mtrl_am_alpha")
    protected Drawable backIcon;
    @OptionsMenuItem(resName = "action_cart")
    protected MenuItem cartMenuItem;

    protected DialogFragment loadingDialog = LoadingDialogFragment_.builder().build();


    @AfterViews
    protected void disableAnimation() {
        overridePendingTransition(0, 0);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final ProjectStore projectStore = data.getProjectStore();
        if (projectStore != null) {
            int textColor = projectStore.getTemplate().getActionBarTextColor();
            if (cartMenuItem != null) {
                View customView = cartMenuItem.getActionView();
                TextView countView = (TextView) customView.findViewById(R.id.cart_count);
                ImageView iconView = (ImageView) customView.findViewById(R.id.cart_icon);
                countView.setText(cartManager.getItemCount() + "");
                iconView.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
                customView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cartManager.getItemCount() == 0) {
                            Toast.makeText(BaseActivity.this, R.string.keep_shopping, Toast.LENGTH_SHORT).show();
                        } else {
                            CartActivity_.intent(BaseActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
                        }
                    }
                });
            }

            backIcon.setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            getSupportActionBar().setHomeAsUpIndicator(backIcon);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(projectStore.getTemplate().getActionBarBgColor())
            );
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            View decor = getWindow().getDecorView();
            ViewGroup actionBar = (ViewGroup) decor.findViewById(getResources().getIdentifier("action_bar_container", "id", getPackageName()));
            if(actionBar!=null) {
                LayoutTraverser.build(new LayoutTraverser.Processor() {
                    @Override
                    public void process(View view) {
                        if(view instanceof TextView && view.getLayoutParams() instanceof Toolbar.LayoutParams){
                            Toolbar.LayoutParams p = (Toolbar.LayoutParams) view.getLayoutParams();
                            p.width= Toolbar.LayoutParams.MATCH_PARENT;
                            p.gravity = Gravity.CENTER;
                            view.setLayoutParams(p);
                            ((TextView)view).setGravity(Gravity.CENTER);
                        }
                    }
                }).traverse(actionBar);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void setTitle(CharSequence title) {
        final ProjectStore projectStore = data.getProjectStore();
        if (projectStore != null) {
            int textColor = projectStore.getTemplate().getActionBarTextColor();
            SpannableString spanTitle = new SpannableString(title);
            spanTitle.setSpan(new ForegroundColorSpan(textColor), 0, spanTitle.length(), 0);
            title = spanTitle;
        }
        super.setTitle(title);
    }

    @OptionsItem({android.R.id.home})
    protected void back() {
        onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
