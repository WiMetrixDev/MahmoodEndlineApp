package com.example.endline.models;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/30/2019.
 */
@SuppressWarnings("serial")
public class fault_model implements Serializable {
    private String fault_id;
    private String fault_code ;
    private String fault_description;
    private String check_status;
    private String fault_count;


    public fault_model(String fault_id, String fault_code, String fault_description, String check_status, String fault_count) {
        this.fault_id = fault_id;
        this.fault_code = fault_code;
        this.fault_description = fault_description;
        this.check_status = check_status;
        this.fault_count = fault_count;
    }

    public String getFault_count() {
        return fault_count;
    }

    public void setFault_count(String fault_count) {
        this.fault_count = fault_count;
    }

    public String getCheck_status() {

        return check_status;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public String getFault_id() {
        return fault_id;
    }

    public void setFault_id(String fault_id) {
        this.fault_id = fault_id;
    }

    public String getFault_code() {
        return fault_code;
    }

    public void setFault_code(String fault_code) {
        this.fault_code = fault_code;
    }

    public String getFault_description() {
        return fault_description;
    }

    public void setFault_description(String fault_description) {
        this.fault_description = fault_description;
    }
    @Override
    public String toString() {
        return fault_code+ " " + fault_description;
    }
}
