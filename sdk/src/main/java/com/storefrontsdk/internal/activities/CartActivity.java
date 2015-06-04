package com.storefrontsdk.internal.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kubatatami.judonetworking.observers.HolderView;
import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.storefrontsdk.R;
import com.storefrontsdk.internal.dialogs.DeleteDialogFragment;
import com.storefrontsdk.internal.dialogs.DeleteDialogFragment_;
import com.storefrontsdk.internal.models.CartProduct;
import com.storefrontsdk.internal.models.Options;
import com.storefrontsdk.internal.utils.CurrencyUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

/**
 * User: Kuba, Jacek
 * Date: 25/01/15
 * Time: 00:09
 */
@EActivity(resName = "activity_cart")
public class CartActivity extends BaseActivity {

    @ViewById(resName = "cart_grid")
    GridView gridView;
    @ViewById(resName = "cart_subtotal_value")
    TextView subtotalValue;

    protected ObserverAdapter<CartProduct> productsAdapter;

    @AfterViews
    protected void start() {
        setTitle(getString(R.string.your_cart));

        productsAdapter = new ObserverAdapter<CartProduct>(this, R.layout.item_cart) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent, Holder.class);
                Holder holder = (Holder) view.getTag();
                final CartProduct cartProduct = getItem(position);
                String optionsText = "";
                for (Options options : cartProduct.getCombo().getOptions()) {
                    optionsText += options.getName() + " ";
                }
                String imageUrl=cartProduct.getComboImage().getMedium();
                ImageLoader.getInstance().displayImage(imageUrl, holder.productImageView);
                holder.productNameView.setText(cartProduct.getProduct().getName());
                holder.productPriceView.setText(CurrencyUtils.getCurrencyValue(cartProduct.getCombo().getPriceModifier()));
                holder.productOptionView.setText(optionsText);
                holder.productNameView.setTextColor(data.getProjectStore().getTemplate().getDescriptionTextColor());
                holder.productPriceView.setTextColor(data.getProjectStore().getTemplate().getDescriptionTextColor());
                view.setBackgroundColor(data.getProjectStore().getTemplate().getProductBgColor());

                holder.productDeleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(cartProduct);
                    }
                });
                holder.productQuantityView.setText(String.valueOf(cartProduct.getQuantity()));
                holder.productQuantityView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductActivity.showQuantityDialog(CartActivity.this,
                                ProductActivity.QUANTITY_LIMIT, new ProductActivity.QuantityDialogListener() {
                                    @Override
                                    public void onQuantityChoosen(int quantity) {
                                        cartProduct.setQuantity(quantity);
                                        refresh();
                                    }
                                });
                    }
                });

                return view;
            }

            @Override
            public int getCount() {
                return cartManager.getProducts().size();
            }

            @Override
            public CartProduct getItem(int position) {
                return cartManager.getProducts().get(position);
            }
        };

        updateTotalValue();

        gridView.setAdapter(productsAdapter);
    }

    @Click(resName = "checkout")
    protected void checkoutClick() {
        FormActivity_.intent(this).start();
    }

    @ItemClick(resName = "cart_grid")
    protected void productClick(CartProduct product) {
        ProductActivity_.intent(this).product(product.getProduct()).start();
    }

    private void refresh() {
        invalidateOptionsMenu();
        productsAdapter.notifyDataSetChanged();
        updateTotalValue();
    }

    private void updateTotalValue() {
        subtotalValue.setText(String.format("%s %.2f", cartManager.getCurrency(),
                cartManager.getProductsTotalCost()));
    }

    private void showDeleteDialog(final CartProduct cartProduct) {
        DeleteDialogFragment_.builder().cartProduct(cartProduct).build().show(getSupportFragmentManager(),"delete_dialog");
    }

    public void onDelete(CartProduct cartProduct) {
        cartManager.removeProduct(cartProduct);
        if (cartManager.getItemCount() == 0) {
            finish();
        } else {
            refresh();
        }
    }

    protected static class Holder {
        @HolderView(resName = "product_image")
        ImageView productImageView;
        @HolderView(resName = "product_name")
        TextView productNameView;
        @HolderView(resName = "product_price")
        TextView productPriceView;
        @HolderView(resName = "product_option")
        TextView productOptionView;
        @HolderView(resName = "product_details")
        View productDetailsView;
        @HolderView(resName = "cart_item_delete")
        View productDeleteView;
        @HolderView(resName = "cart_item_qnty")
        TextView productQuantityView;
    }
}
