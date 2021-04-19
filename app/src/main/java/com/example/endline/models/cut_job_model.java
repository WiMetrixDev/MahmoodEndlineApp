package com.example.endline.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class cut_job_model implements Serializable {
    public String cut_job_id;
    public String cut_job_code;

    public cut_job_model(String cut_job_id, String cut_job_code) {
        this.cut_job_id = cut_job_id;
        this.cut_job_code = cut_job_code;
    }

    public String getCut_job_id() {
        return cut_job_id;
    }

    public void setCut_job_id(String cut_job_id) {
        this.cut_job_id = cut_job_id;
    }

    public String getCut_job_code() {
        return cut_job_code;
    }

    public void setCut_job_code(String cut_job_code) {
        this.cut_job_code = cut_job_code;
    }

    @NonNull
    @Override
    public String toString() {
        return cut_job_code;
    }


}
