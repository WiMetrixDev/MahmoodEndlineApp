package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class order_model implements Serializable {
    private String order_id;
    private String order_code;
    private String color;
    private String size;
    private String style;

    public order_model(String order_id, String order_code, String color, String size, String style) {
        this.order_id = order_id;
        this.order_code = order_code;
        this.color = color;
        this.size = size;
        this.style = style;
    }


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @NonNull
    @Override
    public String toString() {
        return order_code;
    }
}
