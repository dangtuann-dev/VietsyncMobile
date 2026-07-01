package com.app.learning.data.model;

import java.io.Serializable;

public class Category implements Serializable {
    private Long id;
    private String name;
    private int iconResId;
    private String colorHex;
    private String colorLightHex;

    public Category() {
    }

    public Category(Long id, String name, int iconResId, String colorHex, String colorLightHex) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.colorHex = colorHex;
        this.colorLightHex = colorLightHex;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getColorLightHex() {
        return colorLightHex;
    }

    public void setColorLightHex(String colorLightHex) {
        this.colorLightHex = colorLightHex;
    }
}
