package com.example.endline.models;

import java.io.Serializable;

/**
 * Created by Ahsan's Lenovo on 4/29/2019.
 */
@SuppressWarnings("serial")
public class lines_model implements Serializable {
    public String line_id ;
    public String line_code ;
    public String line_desc;

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getLine_code() {
        return line_code;
    }

    public void setLine_code(String line_code) {
        this.line_code = line_code;
    }

    public lines_model(String line_id, String line_code, String line_desc) {
        this.line_id = line_id;
        this.line_code = line_code;
        this.line_desc = line_desc;
    }
    @Override
    public String toString() {
        if(line_code.equals("Please choose")){
            return line_code;
        }
        return "Line Code: "+line_code+" Line Desc: "+line_desc;
    }
}
