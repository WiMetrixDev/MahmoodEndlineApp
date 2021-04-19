package com.example.endline.models;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/29/2019.
 */
@SuppressWarnings("serial")
public class round_model implements Serializable {
    public String round_id ;
    public String round_code ;

    public round_model(String round_id, String round_code) {
        this.round_id = round_id;
        this.round_code = round_code;
    }

    public String getRound_id() {
        return round_id;
    }

    public void setRound_id(String round_id) {
        this.round_id = round_id;
    }

    public String getRound_code() {
        return round_code;
    }

    public void setRound_code(String round_code) {
        this.round_code = round_code;
    }

    @Override
    public String toString() {
        return round_code;
    }
}
