package com.example.endline.models;

public class pending_rework_model {
    String endlinesessionid;
    String cutReportID;
    String cutJobCode;
    String bundleCode;
    String size;
    String bundleQuantity;
    String rejectedPieces;
    String defectedPieces;

    public pending_rework_model(String endlinesessionid, String cutReportID, String cutJobCode, String bundleCode, String size, String bundleQuantity, String rejectedPieces, String defectedPieces) {
        this.endlinesessionid = endlinesessionid;
        this.cutReportID = cutReportID;
        this.cutJobCode = cutJobCode;
        this.bundleCode = bundleCode;
        this.size = size;
        this.bundleQuantity = bundleQuantity;
        this.rejectedPieces = rejectedPieces;
        this.defectedPieces = defectedPieces;
    }

    public String getEndlinesessionid() {
        return endlinesessionid;
    }

    public void setEndlinesessionid(String endlinesessionid) {
        this.endlinesessionid = endlinesessionid;
    }

    public String getCutReportID() {
        return cutReportID;
    }

    public void setCutReportID(String cutReportID) {
        this.cutReportID = cutReportID;
    }

    public String getCutJobCode() {
        return cutJobCode;
    }

    public void setCutJobCode(String cutJobCode) {
        this.cutJobCode = cutJobCode;
    }

    public String getBundleCode() {
        return bundleCode;
    }

    public void setBundleCode(String bundleCode) {
        this.bundleCode = bundleCode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBundleQuantity() {
        return bundleQuantity;
    }

    public void setBundleQuantity(String bundleQuantity) {
        this.bundleQuantity = bundleQuantity;
    }

    public String getRejectedPieces() {
        return rejectedPieces;
    }

    public void setRejectedPieces(String rejectedPieces) {
        this.rejectedPieces = rejectedPieces;
    }

    public String getDefectedPieces() {
        return defectedPieces;
    }

    public void setDefectedPieces(String defectedPieces) {
        this.defectedPieces = defectedPieces;
    }
}
