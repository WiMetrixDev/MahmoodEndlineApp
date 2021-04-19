package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/30/2019.
 */
@SuppressWarnings("serial")
public class activity_model implements Serializable {
   private String activity_id;
   private String allowed_activity;

    public activity_model(String activity_id, String allowed_activity) {
        this.activity_id = activity_id;
        this.allowed_activity = allowed_activity;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getAllowed_activity() {
        return allowed_activity;
    }

    public void setAllowed_activity(String allowed_activity) {
        this.allowed_activity = allowed_activity;
    }
    @NonNull
    @Override
    public String toString() {
        return activity_id;
    }
}
