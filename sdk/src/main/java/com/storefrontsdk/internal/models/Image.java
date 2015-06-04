package com.storefrontsdk.internal.models;

import java.io.Serializable;
import java.util.Map;

public class Image implements Serializable {
    private String small;

    private Map<String, Image> combo_image;

    private String medium;

    private String url;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public Map<String, Image> getComboImage() {
        return combo_image;
    }

    public void setCombo_image(Map<String, Image> combo_image) {
        this.combo_image = combo_image;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
