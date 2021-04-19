package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class size_model implements Serializable {
    public String size_id;
    public String size_code;

    public String getSize_id() {
        return size_id;
    }

    public void setSize_id(String size_id) {
        this.size_id = size_id;
    }

    public String getSize_code() {
        return size_code;
    }

    public void setSize_code(String size_code) {
        this.size_code = size_code;
    }

    public size_model(String size_id, String size_code) {
        this.size_id = size_id;
        this.size_code = size_code;
    }

    @NonNull
    @Override
    public String toString() {
        return size_code;
    }


}
