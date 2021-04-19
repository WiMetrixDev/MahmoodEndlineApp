package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class department_model implements Serializable {

    private String department_id;
    private String department_code;

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    public department_model(String department_id, String department_code) {
        this.department_id = department_id;
        this.department_code = department_code;
    }



    @NonNull
    @Override
    public String toString() {
        return department_code;
    }
}
