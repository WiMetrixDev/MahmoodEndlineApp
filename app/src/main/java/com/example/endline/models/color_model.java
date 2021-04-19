package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class color_model implements Serializable {
    public String order_id ;
    public String color_code ;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public color_model(String order_id, String color_code) {
        this.order_id = order_id;
        this.color_code = color_code;
    }

    @NonNull
    @Override
    public String toString() {
        return color_code;
    }


}
