package com.storefrontsdk.internal.models;

import java.io.Serializable;

/**
 * Created by Kuba on 25/01/15.
 */
public class Status implements Serializable {

    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
