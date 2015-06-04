package com.storefrontsdk.internal.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.storefrontsdk.R;
import com.storefrontsdk.internal.models.CartProduct;
import com.storefrontsdk.internal.models.Combo;
import com.storefrontsdk.internal.models.Group;
import com.storefrontsdk.internal.models.Image;
import com.storefrontsdk.internal.models.Options;
import com.storefrontsdk.internal.models.Product;
import com.storefrontsdk.internal.utils.CurrencyUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jacek
 * Date: 25/01/15
 * Time: 22:39
 */
@EActivity(resName = "activity_product")
public class ProductActivity extends BaseActivity {

    public interface QuantityDialogListener {
        void onQuantityChoosen(int quantity);
    }

    public static final int QUANTITY_LIMIT = 100;

    @SystemService
    LayoutInflater inflater;

    @Extra
    Product product;
    @SystemService
    Vibrator vibrator;

    @ViewById(resName = "product_params_container")
    ViewGroup productParamsContainer;

    @ViewById(resName = "product_photos_view_pager")
    SliderLayout photosSilder;
    @ViewById(resName = "product_price")
    TextView price;
    @ViewById(resName = "product_quantity")
    TextView quantity;
    @ViewById(resName = "product_color")
    TextView color;
    @ViewById(resName = "product_size")
    TextView size;
    @ViewById(resName = "product_description")
    TextView description;

    protected Combo currentCombo;
    private int choosenQuantity;


    @AfterViews
    protected void start() {
        setTitle(product.getName());
        prepareOptionsAndValues();

        updatePriceValue();

        photosSilder.setPresetTransformer(SliderLayout.Transformer.Default);
        photosSilder.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        description.setText(product.getDescription());

        for (Image image : product.getImage()) {
            BaseSliderView baseSliderView = new TextSliderView(this);
            baseSliderView.image(image.getUrl());
            baseSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
            photosSilder.addSlider(baseSliderView);
        }
    }

    @Click(resName = "product_quantity")
    void onQuantityClick() {
        showQuantityDialog(this, QUANTITY_LIMIT, new QuantityDialogListener() {
            @Override
            public void onQuantityChoosen(int quantity) {
                choosenQuantity = quantity;
                handleQuantityClick(choosenQuantity);
            }
        });
    }


    @Click(resName = "product_add_to_cart")
    void onAddToCartClick() {
        if (choosenQuantity == 0) {
            Toast.makeText(this, getString(R.string.product_quantity_needed), Toast.LENGTH_SHORT).show();
        } else {
            vibrator.vibrate(100);
            cartManager.addProduct(new CartProduct(product, choosenQuantity, currentCombo));
            invalidateOptionsMenu();

            Toast.makeText(this, getString(R.string.product_added_to_cart), Toast.LENGTH_SHORT).show();
        }
    }

    public static void showQuantityDialog(Activity activity, int quantityNeeded,
                                          final QuantityDialogListener listener) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setTitle(activity.getString(R.string.product_choose_quantity));
        final CharSequence[] list = new CharSequence[quantityNeeded];
        for (int i = 0; i < quantityNeeded; i++) {
            list[i] = Integer.toString(i + 1);
        }
        builderSingle.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onQuantityChoosen(Integer.decode((String) list[which]));
            }
        });
        builderSingle.show();
    }

    private void updatePriceValue() {
        String toBeSet = String.valueOf(CurrencyUtils.getCurrencyValue(currentCombo.getPriceModifier()));
        price.setText(toBeSet);
    }

    private void handleQuantityClick(int qnty) {
        quantity.setText(String.valueOf(qnty));
    }

    private void prepareOptionsAndValues() {
        Map<String, Group> groups = new HashMap<>();

        for (Combo combo : product.getCombo()) {
            for (Options option : combo.getOptions()) {
                String groupId = option.getGroup().getId();
                groups.put(groupId,option.getGroup());
            }
        }

        for(final Group group : groups.values()){
            TextView paramTextView= (TextView) inflater.inflate(R.layout.item_product_param, productParamsContainer, false);
            paramTextView.setTag(group.getId());
            paramTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGroupClick(group);
                }
            });
            productParamsContainer.addView(paramTextView);
        }

        setCurrentCombo(product.getDefaultCombo());
    }

    private void setCurrentCombo(Combo combo){
        this.currentCombo=combo;
        for(Options options : combo.getOptions()){
            TextView textView = (TextView) productParamsContainer.findViewWithTag(options.getGroup().getId());
            textView.setText(options.getName());
        }
        checkOptionAvailable();
        updatePriceValue();
    }
    private void checkOptionAvailable(){
        for(Options options : currentCombo.getOptions()){
            TextView textView = (TextView) productParamsContainer.findViewWithTag(options.getGroup().getId());
            boolean enabled = checkOptionAvailable(options.getGroup());
            textView.setEnabled(enabled);
            textView.setTextColor(enabled ? Color.WHITE : Color.GRAY);
        }
    }

    private boolean checkOptionAvailable(Group group){
        int count=0;

        for (Combo combo : product.getCombo()) {
            boolean addOption=true;
            for (Options option : combo.getOptions()) {
                if(!option.getGroup().getId().equals(group.getId())){
                    for(Options currentOption : currentCombo.getOptions()){
                        if(currentOption.getGroup().getId().equals(option.getGroup().getId())){
                            if(!option.getGroup().getId().equals(group.getId())){
                                if(!option.getName().equals(currentOption.getName())){
                                    addOption=false;
                                }
                            }
                        }
                    }
                }
            }
            if(addOption){
                count++;
            }
        }
        return count>1;

    }

    private void onGroupClick(Group group) {
        final List<OptionCombo> items = new ArrayList<>();

        for (Combo combo : product.getCombo()) {
            boolean addOption=true;
            Options finalOption=null;
            for (Options option : combo.getOptions()) {
                if(option.getGroup().getId().equals(group.getId())){
                    finalOption = option;
                }else{
                    for(Options currentOption : currentCombo.getOptions()){
                        if(currentOption.getGroup().getId().equals(option.getGroup().getId())){
                            if(!option.getGroup().getId().equals(group.getId())){
                                if(!option.getName().equals(currentOption.getName())){
                                    addOption=false;
                                }
                            }
                        }
                    }
                }
            }
            if(addOption){
                items.add(new OptionCombo(combo,finalOption));
            }
        }



        if (items.size() == 1) {
            Toast.makeText(this, getString(R.string.product_this_product_only_one,
                    getString(R.string.product_size)), Toast.LENGTH_SHORT).show();
        } else {
            Collections.sort(items);
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(getString(R.string.product_choose_size));
            builderSingle.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setCurrentCombo(items.get(which).combo);
                }
            });
            builderSingle.show();
        }

    }

    static class OptionCombo implements Comparable<OptionCombo>{
        Combo combo;
        Options options;

        OptionCombo(Combo combo, Options options) {
            this.combo = combo;
            this.options = options;
        }

        @Override
        public String toString() {
            return options.getName();
        }

        @Override
        public int compareTo(OptionCombo another) {
            return Integer.decode(options.getId()).compareTo(Integer.decode(another.options.getId()));
        }
    }

}
