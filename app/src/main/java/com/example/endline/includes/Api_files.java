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
    public String endline_session = "/SQMS/endline/createEndlineSession";
    public String getOperationWorkerForBundle = "/SQMS/endline/getOperationAndWorkersDetailsByBundleID";

    public String registerFaults = "/SQMS/Endline/registerFaults";


    public String close_endline_session = "/SQMS/endline/closeEndlineSession";

    public String getDetailsForBundleCard = "/SQMS/Endline/getBundlesForTagID";

    public String fetch_cut_history_crID_section = "Endline/getHistoryForCRIDSection.php";
}
