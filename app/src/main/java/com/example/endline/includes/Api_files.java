package com.example.endline.includes;

public class Api_files {

    public String user_login = "/Login/Login";
    public String get_all_lines = "/lines/get";
    public String getPOs = "/ProductionOrder/get";
    public String get_faults = "/SQMS/Inline/getFaults";

    public String getColorsForOrderCodeStyleCode = "/bundleReport/getColorsForOrderCodeStyleCode";

    public String getJobCardsForOrderID = "/cutJob/get";
    public String getSizesForOrderIDJobCard = "/bundleReport/getSizesForOrderIDJobCard";
    public String getBundlesForCutJob = "/SQMS/Endline/getBundlesForCutJobID";
    public String endline_session = "/endline/addEndlineSession";
    public String getOperationWorkerForBundle = "/endline/getOperationsAndWorkersForBundleID";


    public String submit_faults = "/endline/add";
    public String close_endline_session = "/endline/closeEndlineSession";

    public String getDetailsForBundleCard = "/endline/getDetailsForBundleCard";

    public String fetch_cut_history_crID_section = "Endline/getHistoryForCRIDSection.php";
}
