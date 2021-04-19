package com.example.endline.models;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 5/8/2019.
 */
@SuppressWarnings("serial")
public class bundle_model implements Serializable {

    private String bundle_code;
    private String bundle_status;
    private String bundle_id;
    private String bundle_qty;
    private String faulty_pieces;
    private String rejected_pieces;
    private String rework_state;
    private String session_id;

    public bundle_model(String bundle_code, String bundle_status, String bundle_id, String bundle_qty, String faulty_pieces, String rejected_pieces, String rework_state, String session_id) {
        this.bundle_code = bundle_code;
        this.bundle_status = bundle_status;
        this.bundle_id = bundle_id;
        this.bundle_qty = bundle_qty;
        this.faulty_pieces = faulty_pieces;
        this.rejected_pieces = rejected_pieces;
        this.rework_state = rework_state;
        this.session_id = session_id;
    }

    public String getBundle_code() {
        return bundle_code;
    }

    public void setBundle_code(String bundle_code) {
        this.bundle_code = bundle_code;
    }

    public String getBundle_status() {
        return bundle_status;
    }

    public void setBundle_status(String bundle_status) {
        this.bundle_status = bundle_status;
    }

    public String getBundle_id() {
        return bundle_id;
    }

    public void setBundle_id(String bundle_id) {
        this.bundle_id = bundle_id;
    }

    public String getBundle_qty() {
        return bundle_qty;
    }

    public void setBundle_qty(String bundle_qty) {
        this.bundle_qty = bundle_qty;
    }

    public String getFaulty_pieces() {
        return faulty_pieces;
    }

    public void setFaulty_pieces(String faulty_pieces) {
        this.faulty_pieces = faulty_pieces;
    }

    public String getRejected_pieces() {
        return rejected_pieces;
    }

    public void setRejected_pieces(String rejected_pieces) {
        this.rejected_pieces = rejected_pieces;
    }

    public String getRework_state() {
        return rework_state;
    }

    public void setRework_state(String rework_state) {
        this.rework_state = rework_state;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @Override
    public String toString() {
        return bundle_code + " " + bundle_status;
    }
}
