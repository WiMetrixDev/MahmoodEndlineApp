package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/30/2019.
 */
@SuppressWarnings("serial")
public class module_model implements Serializable {
   private   String module_id;
   private String allowed_module_id;

    public module_model(String module_id, String allowed_module_id) {
        this.module_id = module_id;
        this.allowed_module_id = allowed_module_id;
    }
    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getAllowed_module_id() {
        return allowed_module_id;
    }

    public void setAllowed_module_id(String allowed_module_id) {
        this.allowed_module_id = allowed_module_id;
    }
    @NonNull
    @Override
    public String toString() {
        return module_id;
    }
}
