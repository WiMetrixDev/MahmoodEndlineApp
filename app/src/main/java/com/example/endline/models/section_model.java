package com.example.endline.models;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/29/2019.
 */
@SuppressWarnings("serial")
public class section_model implements Serializable {
    public String section_id ;
    public String section_code ;

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getSection_code() {
        return section_code;
    }

    public void setSection_code(String section_code) {
        this.section_code = section_code;
    }

    public section_model(String section_id, String section_code) {
        this.section_id = section_id;
        this.section_code = section_code;
    }
    @Override
    public String toString() {
        return section_code;
    }
}
