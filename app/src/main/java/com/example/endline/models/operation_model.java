package com.example.endline.models;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/29/2019.
 */
@SuppressWarnings("serial")
public class operation_model implements Serializable {
    public String bundle_scan_id;
    public  String operation_id;
    public  String operation_code;
    public  String operation_description;
    public String worker_id ;
    public String worker_code ;
    public String worker_name ;

    public String getBundle_scan_id() {
        return bundle_scan_id;
    }

    public void setBundle_scan_id(String bundle_scan_id) {
        this.bundle_scan_id = bundle_scan_id;
    }

    public String getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(String operation_id) {
        this.operation_id = operation_id;
    }

    public String getOperation_code() {
        return operation_code;
    }

    public void setOperation_code(String operation_code) {
        this.operation_code = operation_code;
    }

    public String getOperation_description() {
        return operation_description;
    }

    public void setOperation_description(String operation_description) {
        this.operation_description = operation_description;
    }

    public String getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(String worker_id) {
        this.worker_id = worker_id;
    }

    public String getWorker_code() {
        return worker_code;
    }

    public void setWorker_code(String worker_code) {
        this.worker_code = worker_code;
    }

    public String getWorker_name() {
        return worker_name;
    }

    public void setWorker_name(String worker_name) {
        this.worker_name = worker_name;
    }

    public operation_model(String bundle_scan_id, String operation_id, String operation_code, String operation_description, String worker_id, String worker_code, String worker_name) {
        this.bundle_scan_id = bundle_scan_id;
        this.operation_id = operation_id;
        this.operation_code = operation_code;
        this.operation_description = operation_description;
        this.worker_id = worker_id;
        this.worker_code = worker_code;
        this.worker_name = worker_name;
    }

    @Override
    public String toString() {
        return operation_description;
    }
}
