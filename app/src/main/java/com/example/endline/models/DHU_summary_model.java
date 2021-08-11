package com.example.endline.models;

public class DHU_summary_model {
    String CheckedPieces;
    String NoOfFaults;
    String DHU;

    public DHU_summary_model(String checkedPieces, String noOfFaults, String DHU) {
        CheckedPieces = checkedPieces;
        NoOfFaults = noOfFaults;
        this.DHU = DHU;
    }

    public String getCheckedPieces() {
        return CheckedPieces;
    }

    public void setCheckedPieces(String checkedPieces) {
        CheckedPieces = checkedPieces;
    }

    public String getNoOfFaults() {
        return NoOfFaults;
    }

    public void setNoOfFaults(String noOfFaults) {
        NoOfFaults = noOfFaults;
    }

    public String getDHU() {
        return DHU;
    }

    public void setDHU(String DHU) {
        this.DHU = DHU;
    }
}
