package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class MilestoneModel {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("achieved_date")
    private String achievedDate;

    @SerializedName("icon_res_name")
    private String iconResName;

    public MilestoneModel() {}

    public MilestoneModel(String id, String title, String description, String achievedDate, String iconResName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.achievedDate = achievedDate;
        this.iconResName = iconResName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAchievedDate() { return achievedDate; }
    public void setAchievedDate(String achievedDate) { this.achievedDate = achievedDate; }

    public String getIconResName() { return iconResName; }
    public void setIconResName(String iconResName) { this.iconResName = iconResName; }
}
