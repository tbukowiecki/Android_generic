package com.storefrontsdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kubatatami.judonetworking.callbacks.ActivityCallback;
import com.github.kubatatami.judonetworking.exceptions.JudoException;
import com.github.kubatatami.judonetworking.observers.HolderView;
import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.storefrontsdk.internal.Configuration;
import com.storefrontsdk.internal.activities.BaseActivity;
import com.storefrontsdk.internal.activities.ProductActivity_;
import com.storefrontsdk.internal.connection.Connection_;
import com.storefrontsdk.internal.models.Product;
import com.storefrontsdk.internal.models.ProjectStore;
import com.storefrontsdk.internal.utils.CurrencyUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


@EActivity(resName = "activity_shop")
public class ShopActivity extends BaseActivity {

    @SystemService
    protected LayoutInflater inflater;

    @Extra
    protected Environment environment;
    @Extra
    protected String sdkProjectSecret;
    @Extra
    protected int sdkProjectId;

    @ViewById(resName = "progress")
    protected View progressView;
    @ViewById(resName = "product_list")
    protected AbsListView productListView;

    protected View footer;
    protected ObserverAdapter<Product> productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            Connection_.getInstance_(this).clearCache();
        }
    }

    @AfterViews
    protected void start() {
        if (data.getProjectId() != sdkProjectId) {
            cartManager.clear();
        }
        data.setProjectId(sdkProjectId);
        setTitle("");
        getSupportActionBar().hide();
        productAdapter = new ObserverAdapter<Product>(this, R.layout.item_product) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent, Holder.class);
                Holder holder = (Holder) view.getTag();
                Product product = getItem(position);
                ImageLoader.getInstance().displayImage(product.getImage()[0].getMedium(), holder.productImageView);
                holder.productNameView.setText(product.getName());
                holder.productPriceView.setText(CurrencyUtils.getCurrencyValue(product.getCommission()));
                holder.productNameView.setTextColor(data.getProjectStore().getTemplate().getDescriptionTextColor());
                holder.productPriceView.setTextColor(data.getProjectStore().getTemplate().getDescriptionTextColor());
                holder.productDetailsView.setBackgroundColor(data.getProjectStore().getTemplate().getDescriptionBgColor());
                view.setBackgroundColor(data.getProjectStore().getTemplate().getProductBgColor());
                return view;
            }
        };
        footer = inflater.inflate(R.layout.footer_terms, null, false);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Configuration.TERM_OF_SERVICE)));
            }
        });
        if (productListView instanceof ListView) {
            ((ListView) productListView).addFooterView(footer);
        } else if (productListView instanceof GridViewWithHeaderAndFooter) {
            ((GridViewWithHeaderAndFooter) productListView).addFooterView(footer);
        }
        productListView.setAdapter(productAdapter);
        connection.createEndpoint(environment, sdkProjectSecret, sdkProjectId);
        connection.getApi().getViewProjectStore(new ActivityCallback<ProjectStore>(this) {
            @Override
            public void onSafeSuccess(ProjectStore result) {
                if (result != null) {
                    data.setProjectStore(result);
                    productAdapter.preHoneycombAddAll(true, result.getProducts());
                    footer.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setTitle(result.getName());
                    getSupportActionBar().show();
                } else {
                    Toast.makeText(ShopActivity.this, R.string.no_produts, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onSafeError(JudoException e) {
                finish();
            }

            @Override
            public void onSafeFinish() {
                progressView.setVisibility(View.GONE);
            }
        });
    }


    @ItemClick(resName = "product_list")
    protected void productClick(Product product) {
        ProductActivity_.intent(this).product(product).start();
    }



    protected static class Holder {
        @HolderView(resName = "product_image")
        ImageView productImageView;
        @HolderView(resName = "product_name")
        TextView productNameView;
        @HolderView(resName = "product_price")
        TextView productPriceView;
        @HolderView(resName = "product_details")
        View productDetailsView;
    }

}
