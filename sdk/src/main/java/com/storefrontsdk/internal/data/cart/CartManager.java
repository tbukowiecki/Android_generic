package com.storefrontsdk.internal.data.cart;

import android.content.Context;

import com.storefrontsdk.internal.data.DataContainer_;
import com.storefrontsdk.internal.models.CartProduct;
import com.storefrontsdk.internal.utils.CurrencyUtils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Kuba, Jacek
 * Date: 26/01/15
 * Time: 23:22
 */
@EBean(scope = EBean.Scope.Singleton)
public class CartManager {

    private static final String RECIPIENT_FILENAME = "recipient_data";

    @RootContext
    Context context;

    private CartRecipientModel recipient = new CartRecipientModel();
    private CartModel cartModel = new CartModel();

    public void clear() {
        // TODO czyść zapisany obiekt!?
        cartModel = new CartModel();
        recipient = new CartRecipientModel();
    }

    public int getItemCount() {
        int count = 0;
        for (CartProduct product : cartModel.products) {
            count += product.getQuantity();
        }
        return count;
    }

    public void addProduct(CartProduct product) {
        int index = cartModel.products.indexOf(product);
        if (index != -1) {
            cartModel.products.get(index).addQuantity(product.getQuantity());
        } else {
            cartModel.products.add(product);
        }
    }

    public List<CartProduct> getProducts() {
        return cartModel.products;
    }

    public CartRecipientModel getRecipient() {
        return recipient;
    }

    public void removeProduct(CartProduct product) {
        cartModel.products.remove(product);
    }

    public void storeRecipientModel() {
        saveRecipientToFile();
    }

    public void loadRecipientModel() {
        recipient = loadRecipientFromFile();

        if (recipient == null) {
            recipient = new CartRecipientModel();
        }
    }

    private void saveRecipientToFile() {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            fos = context.openFileOutput(RECIPIENT_FILENAME, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(recipient);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignored) {

            }
        }
    }

    private CartRecipientModel loadRecipientFromFile() {
        CartRecipientModel result = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try {
            fis = context.openFileInput(RECIPIENT_FILENAME);
            is = new ObjectInputStream(fis);
            result = (CartRecipientModel) is.readObject();
            is.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {

            }
        }
        return result;
    }

    public double getProductsTotalCost() {
        double totalAmount = 0;
        for (CartProduct product : getProducts()) {
            totalAmount += product.getPriceTotal();
        }

        return totalAmount;

    }

    public List<NameValuePair> getProductsMap() {
        List<NameValuePair> productArray = new ArrayList<>();


        //Ugly code ports from C# implementation
        for (CartProduct product : getProducts()) {
            if (product.getProduct().getCombo().length > 0) {
                productArray.add(new BasicNameValuePair("purchase_items[" + product.getProduct().getId() + "][" + product.getCombo().getId() + "]", product.getQuantity() + ""));
            } else {
                productArray.add(new BasicNameValuePair("purchase_items[" + product.getProduct().getId() + "]", product.getQuantity() + ""));
            }
        }
        String storeId = DataContainer_.getInstance_(context).getProjectStore().getId() + "";
        for (CartProduct product : getProducts()) {
            if (product.getProduct().getCombo().length > 0) {
                productArray.add(new BasicNameValuePair("store_ids[" + storeId + "][" + +product.getProduct().getId() + "][" + product.getCombo().getId() + "]", product.getQuantity() + ""));
            } else {
                productArray.add(new BasicNameValuePair("store_ids[" + storeId + "][" + +product.getProduct().getId() + "]", product.getQuantity() + ""));
            }
        }
        return productArray;
    }

    public String getCurrency() {
        return CurrencyUtils.getCurrency(getProducts().get(0).getCombo().getPriceModifier());
    }
}
