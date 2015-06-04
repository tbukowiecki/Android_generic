package com.storefrontsdk.internal.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.storefrontsdk.R;
import com.storefrontsdk.internal.activities.CartActivity;
import com.storefrontsdk.internal.models.CartProduct;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class DeleteDialogFragment extends DialogFragment {

    @FragmentArg
    protected CartProduct cartProduct;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setMessage(getString(R.string.cart_delete));
        builderSingle.setNegativeButton(getString(R.string.cart_no), null);
        builderSingle.setPositiveButton(getString(R.string.cart_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((CartActivity)getActivity()).onDelete(cartProduct);
            }
        });
        return builderSingle.create();
    }
}