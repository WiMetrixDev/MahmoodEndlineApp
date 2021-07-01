package com.example.endline;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.endline.Reader.NFCReaderService;
import com.example.endline.includes.Api_files;
import com.example.endline.includes.IP;
import com.example.endline.models.bundle_model;
import com.example.endline.models.cut_job_model;
import com.example.endline.models.fault_model;
import com.example.endline.models.lines_model;
import com.example.endline.models.order_model;
import com.example.endline.models.section_model;
import com.example.endline.models.size_model;
import com.example.endline.utils.Converter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Bundle_Selection_Activity extends AppCompatActivity {
    PendingIntent mPendingIntent;
    NfcAdapter mNfcAdapter;
    IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
    String allowed_module_id;
    String user_id;
    IntentFilter[] mFilters = new IntentFilter[]{
            ndef,
    };
    // Setup a tech list for all NfcF tags
    String[][] mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}};
    Button submitbtn;
    ArrayList<bundle_model> bundle_list = new ArrayList<>();
    ArrayList<String> size_list = new ArrayList<>();
    ArrayList<order_model> order_list = new ArrayList<>();
    ArrayList<fault_model> fault_list = new ArrayList<>();
    ArrayList<cut_job_model> cut_job_list = new ArrayList<>();
    ProgressDialog nDialog;
    SearchableSpinner lot_spinner;
    SearchableSpinner bundle_spinner;
    SearchableSpinner order_spinner;
    SearchableSpinner spinner_size;
    lines_model Lines_extra;
    TextView text_Line;
    TextView text_Section;
    IP ip;
    section_model section;
    Api_files api = new Api_files();
    Receiver receiver = new Receiver();
    Intent reader_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle__selection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Bundle_Selection_Activity.this, Line_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String IP = sp.getString("IP", null);
        allowed_module_id = sp.getString("allowedModuleID", null);
        user_id = sp.getString("userID", null);
        ip = new IP(IP);
        section = new section_model(sp.getString("sectionID", null), sp.getString("sectionCode", null));
        get_views();
        get_extras();
        layout_listeners();
        layout_spinners();
        fetch_orders();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            IntentFilter filter = new IntentFilter("nfc.tag");
            getApplicationContext().registerReceiver(receiver, filter);
        } else {
            if (!mNfcAdapter.isEnabled()) {
                // Stop here, we definitely need NFC
                Toast.makeText(this, " NFC not enabled!", Toast.LENGTH_LONG).show();
            } else {
                mPendingIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                try {
                    ndef.addDataType("*/*");
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    throw new RuntimeException("fail", e);
                }
            }
        }
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int TagNumber = arg1.getIntExtra("TagNumber", -1);
            int TagType = arg1.getIntExtra("TagType", -1);
            try {
                if(TagType == 0){
                    fetch_job_card_data(String.valueOf(TagNumber), allowed_module_id,user_id, Lines_extra);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Card Type! " + TagType,
                            Toast.LENGTH_SHORT).show();
                }
                System.out.println(TagNumber + "=========" + TagType);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bundle_spinner.setAdapter(null);
        if (order_spinner.getSelectedItemPosition() > 0) {
            final order_model PO = (order_model) order_spinner.getSelectedItem();
            cut_job_model Lot = (cut_job_model) lot_spinner.getSelectedItem();
                fetch_bundle(PO.getOrder_id(), Lot.cut_job_id);
        }
        try {
            if (mNfcAdapter == null) {
                // Stop here, we definitely need NFC
                startService(new Intent(this, NFCReaderService.class));
                IntentFilter filter = new IntentFilter("nfc.tag");
                getApplicationContext().registerReceiver(receiver, filter);
            }
            else {
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            getApplicationContext().unregisterReceiver(receiver);
        } catch (Exception ignored){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getApplicationContext().unregisterReceiver(receiver);
        } catch (Exception ignored){

        }
        stopService(new Intent(this, NFCReaderService.class));
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;

            try {
                mfc.connect();
                boolean auth;
                int bIndex = 5;
                auth = mfc.authenticateSectorWithKeyA(1, MifareClassic.KEY_DEFAULT);
                if (auth) {
                    data = mfc.readBlock(bIndex);
                    String readData = Converter.byteArrayToHexString(data);
                    int card_type = Integer.parseInt(readData.substring(9,10));
                    int card_id = ((0xFF & data[3]) << 24) | ((0xFF & data[2]) << 16) | ((0xFF & data[1]) << 8) | ((0xFF & data[0]));
                    if (card_type == 0) {
                        card_id = Integer.parseInt(readData.substring(0, 8));
                        System.out.println("tagID "+card_id);
                        fetch_job_card_data(String.valueOf(card_id), allowed_module_id,user_id,Lines_extra);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Card Type! " + card_type,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Card Read Exception! " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void get_views() {
        submitbtn = findViewById(R.id.submitbtn);
        order_spinner = findViewById(R.id.spinner_po);
        lot_spinner = findViewById(R.id.spinner_lot);
        bundle_spinner = findViewById(R.id.spinner_bundle);
        text_Line = findViewById(R.id.text_Line);
        text_Section = findViewById(R.id.text_Section);
        spinner_size = findViewById(R.id.spinner_size);
    }
    public void layout_listeners() {
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(bundle_spinner.getSelectedItemPosition());
                if (order_spinner.getSelectedItemPosition() > 0 && lot_spinner.getSelectedItemPosition() > 0 && spinner_size.getSelectedItemPosition() > 0  && bundle_spinner.getSelectedItemPosition() > 0) {
                    final String Cut = lot_spinner.getSelectedItem().toString();
                    final bundle_model Bundle = (bundle_model) bundle_spinner.getSelectedItem();
                    final order_model PO = (order_model) order_spinner.getSelectedItem();
                    if (Bundle.getSession_id().equals("-1") || Bundle.getRework_state().equals("-1")) {
                        Bundle.setRework_state("0");
                        endline_session_login(allowed_module_id, Lines_extra, PO, Cut, Bundle);
                    } else {
                        if (Bundle.getRework_state().equals("0")) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Bundle_Selection_Activity.this);
                            alertDialog.setMessage("Are you performing REWORK for this bundle?")
                                    .setCancelable(false)
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Bundle.setRework_state("0");
                                            endline_session_login(allowed_module_id, Lines_extra, PO, Cut, Bundle);
                                        }
                                    })
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Bundle.setRework_state("1");
                                            endline_session_login(allowed_module_id, Lines_extra, PO, Cut, Bundle);
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        } else if (Bundle.getRework_state().equals("1")) {
                            Bundle.setRework_state("1");
                            endline_session_login(allowed_module_id, Lines_extra, PO, Cut, Bundle);

                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Incomplete Form",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void get_extras() {
        Intent i = getIntent();
        Lines_extra = (lines_model) i.getSerializableExtra("Line");
        Bundle lists_bundle = i.getBundleExtra("Lists");
        fault_list = (ArrayList<fault_model>) lists_bundle.getSerializable("Faults_List");
        text_Line.setText(Lines_extra.line_desc);
        text_Section.setText(section.getSection_code());
    }
    public void showLoader() {
        nDialog = new ProgressDialog(Bundle_Selection_Activity.this);
        nDialog.setMessage("Please Wait..");
        nDialog.setTitle("Fetching Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();
    }
    public void hideLoader() {
        nDialog.hide();
        nDialog.cancel();
    }
    public void layout_spinners() {
        order_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lot_spinner.setAdapter(null);
                bundle_spinner.setAdapter(null);
                spinner_size.setAdapter(null);
                if(order_spinner.getSelectedItemPosition()>0){
                    order_model order = (order_model) order_spinner.getSelectedItem();
                    fetch_cut_jobs(order.getOrder_id());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        lot_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bundle_spinner.setAdapter(null);
                spinner_size.setAdapter(null);
                if (lot_spinner.getSelectedItemPosition() > 0) {
                    order_model order = (order_model) order_spinner.getSelectedItem();
                    cut_job_model Lot = (cut_job_model) lot_spinner.getSelectedItem();
                    fetch_bundle(order.getOrder_id(),Lot.cut_job_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bundle_spinner.setAdapter(null);
                if (spinner_size.getSelectedItemPosition() > 0) {
                    ArrayList<bundle_model> filterd_bundle_list = new ArrayList<>();
                    for (int j=0; j<bundle_list.size(); j++){
                        if(bundle_list.get(i).getSize().equals(spinner_size.getSelectedItem())){
                            filterd_bundle_list.add(bundle_list.get(j));
                        }
                    }
                    ArrayAdapter<bundle_model> dataAdapter = new ArrayAdapter<>(Bundle_Selection_Activity.this, android.R.layout.simple_spinner_item, filterd_bundle_list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bundle_spinner.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        bundle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void fetch_orders() {
        showLoader();
        order_list.clear();
        HashMap<String, String> params = new HashMap<String, String>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, ip.getIp() +api.getPOs, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                JSONArray s = response.getJSONArray("data");
                                order_list.add(new order_model("", "Please Choose", "", "", ""));
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);
                                    System.out.println("==="+res);
                                    String order_id = res.getString("productionOrderID");
                                    String po_no = res.getString("productionOrderCode");
                                    String color = res.getString("color");
                                    String size = "";
                                    String style = res.getString("styleCode");
                                    order_list.add(new order_model(order_id, po_no, color, size, style));
                                }

                                ArrayAdapter<order_model> dataAdapter = new ArrayAdapter<>(Bundle_Selection_Activity.this, android.R.layout.simple_spinner_item, order_list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                order_spinner.setAdapter(dataAdapter);
                                hideLoader();
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to fetch Production Order: " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), "Unexpected response, check server file",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    public void fetch_cut_jobs(final String Order) {
        showLoader();
        cut_job_list.clear();
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("productionOrderID",Order);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.getJobCardsForOrderID, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                JSONArray s = response.getJSONArray("data");
                                cut_job_list.add(new cut_job_model("", "Please Choose"));
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);
                                    String cutJobID = res.getString("cutJobID");
                                    String cutJobCode = res.getString("cutJobCode");
                                    cut_job_list.add(new cut_job_model(cutJobID, cutJobCode));
                                }
                                ArrayAdapter<cut_job_model> dataAdapter = new ArrayAdapter<>(Bundle_Selection_Activity.this, android.R.layout.simple_spinner_item, cut_job_list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                lot_spinner.setAdapter(dataAdapter);
                                hideLoader();

                                hideLoader();
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to fetch Job Cards: " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), "Unexpected response, check server file",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public void fetch_size(){
        size_list.clear();
        size_list.add("Please Choose");
        for (int i=1; i<bundle_list.size(); i++){
            if(!size_list.contains(bundle_list.get(i).getSize())) {
                size_list.add(bundle_list.get(i).getSize());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Bundle_Selection_Activity.this, android.R.layout.simple_spinner_item, size_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_size.setAdapter(dataAdapter);
    }


    public void fetch_bundle(final String OrderID, final String Lot) {
        showLoader();
        bundle_list.clear();
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("cutJobID",Lot);
        params.put("sectionID", section.section_id);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.getBundlesForCutJob, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                JSONArray s = response.getJSONArray("data");
                                bundle_list.add(new bundle_model("", "Please Choose", "", "0", "", "", "", "","",""));
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);
                                    System.out.println(res);
                                    String bundle_code = res.getString("bundleCode");
                                    String bundle_status = "";
                                    String bundle_id = res.getString("bundleID");
                                    String bundle_qty = res.getString("bundleQuantity");
                                    String rework_state = res.getString("reworkState");
                                    String defected_pieces =  res.getString("defectedPieces");
                                    String rejectedPieces =  res.getString("rejectedPieces");
                                    String size =  res.getString("size");
                                    String shade =  res.getString("shade");

                                    if (rework_state.equals("-1")) {
                                        bundle_status = "NOT CHECKED";
                                    }
                                    if (rework_state.equals("0")) {
                                        bundle_status = "ALREADY CHECKED";
                                    }
                                    if (rework_state.equals("1")) {
                                        bundle_status = "REWORKED";
                                    }
                                    bundle_list.add(new bundle_model(bundle_code, bundle_status, bundle_id, bundle_qty, defected_pieces, rejectedPieces, rework_state, "",size,shade));
                                }
//                                ArrayAdapter<bundle_model> dataAdapter = new ArrayAdapter<>(Bundle_Selection_Activity.this, android.R.layout.simple_spinner_item, bundle_list);
//                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                bundle_spinner.setAdapter(dataAdapter);

                                fetch_size();
                                hideLoader();
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to fetch Bundles: " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), "Unexpected response, check server file",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoader();
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    public void onBackPressed() {
        Intent intent = new Intent(this, Line_Activity.class);
        startActivity(intent);
    }
    public void fetch_job_card_data(final String ItemID, final String user_permission_id, final String user_id ,final lines_model Line) {
        final HashMap<String, String> params = new HashMap<String, String>();
        System.out.println("ItemID======"+ItemID);
        params.put("tagID", ItemID);
        params.put("userID", user_id);
        params.put("lineID", Line.getLine_id());
        params.put("allowedModuleID", allowed_module_id);
        params.put("sectionID", section.section_id);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.getDetailsForBundleCard, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                JSONObject  s = response.getJSONObject("data");
                                System.out.println(s);
                                String orderID = s.getString("productionOrderID");
                                String style = s.getString("styleCode");
                                String orderCode = s.getString("productionOrderCode");
                                final String size_id = s.getString("size");
                                final String size_code = s.getString("size");
                                String color = s.getString("color");
                                final String lotCode = s.getString("cutJobCode");
                                String bundleID = s.getString("cutReportID");
                                String bundleCode = s.getString("bundleCode");
                                String bundleQuantity = s.getString("bundleQuantity");
                                String endlineSessionId = s.getString("endLineSessionID");
                                String reworkState = s.getString("reworkState");
                                String faultyPieces = s.getString("defectedPieces");
                                String rejectedPieces = s.getString("rejectedPieces");
                                //String shade = s.getString("shade");
                                String shade = "";

                                String bundle_status = "";
                                System.out.println("b==="+bundleCode);
                                if (reworkState.equals("-1")) {
                                    bundle_status = "NOT CHECKED";
                                }
                                if (reworkState.equals("0")) {
                                    bundle_status = "ALREADY CHECKED";
                                }
                                if (reworkState.equals("1")) {
                                    bundle_status = "REWORKED";
                                }
                                final order_model PO = new order_model(orderID, orderCode, color, size_code, style);
                                final bundle_model Bundle = new bundle_model(bundleCode, bundle_status, bundleID, bundleQuantity, faultyPieces, rejectedPieces, reworkState, endlineSessionId, size_code,shade );
                                bundle_list.add(Bundle);
                                if (reworkState.equals("-1")) {
                                    Bundle.setRework_state("0");
                                    Intent intent = new Intent(Bundle_Selection_Activity.this, Fault_Submission_Activity.class);
                                    intent.putExtra("PO", PO);
                                    intent.putExtra("Line", Lines_extra);
                                    intent.putExtra("Size", new size_model(size_id,size_code));
                                    //intent.putExtra("Section", Section_extra);
                                    Bundle lists_bundle = new Bundle();
                                    //lists_bundle.putSerializable("Departments_List", department_list);
                                    lists_bundle.putSerializable("Faults_List", fault_list);
                                    intent.putExtra("Lists", lists_bundle);
                                    intent.putExtra("Lot", lotCode);
                                    intent.putExtra("Bundle", Bundle);
                                    intent.putExtra("Session_id", Bundle.getSession_id());
                                    startActivity(intent);
                                } else if (reworkState.equals("0")) {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Bundle_Selection_Activity.this);
                                    alertDialog.setMessage("Are you performing REWORK for this bundle?")
                                            .setCancelable(false)
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    Bundle.setRework_state("0");
                                                    Intent intent = new Intent(Bundle_Selection_Activity.this, Fault_Submission_Activity.class);
                                                    intent.putExtra("PO", PO);
                                                    intent.putExtra("Line", Lines_extra);
                                                    intent.putExtra("Lot", lotCode);
                                                    intent.putExtra("Size", new size_model(size_id,size_code));
                                                    Bundle lists_bundle = new Bundle();
                                                    lists_bundle.putSerializable("Faults_List", fault_list);
                                                    intent.putExtra("Lists", lists_bundle);
                                                    intent.putExtra("Bundle", Bundle);
                                                    intent.putExtra("Session_id", Bundle.getSession_id());
                                                    startActivity(intent);
                                                }
                                            })
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    Bundle.setRework_state("1");
                                                    endline_session_login(user_permission_id, Lines_extra, PO, lotCode, Bundle);
                                                }
                                            });
                                    AlertDialog alert = alertDialog.create();
                                    alert.show();
                                } else if (Bundle.getRework_state().equals("1")) {
                                    Bundle.setRework_state("1");
                                    Intent intent = new Intent(Bundle_Selection_Activity.this, Fault_Submission_Activity.class);
                                    intent.putExtra("PO", PO);
                                    intent.putExtra("Line", Lines_extra);
                                    intent.putExtra("Lot", lotCode);
                                    intent.putExtra("Size", new size_model(size_id,size_code));
                                    Bundle lists_bundle = new Bundle();
                                    //lists_bundle.putSerializable("Departments_List", department_list);
                                    lists_bundle.putSerializable("Faults_List", fault_list);
                                    intent.putExtra("Lists", lists_bundle);
                                    intent.putExtra("Bundle", Bundle);
                                    intent.putExtra("Session_id", Bundle.getSession_id());
                                    startActivity(intent);
                                }
                                hideLoader();
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to fetch Bundles: " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            System.out.println(e.getMessage());
                            Toast.makeText(getApplicationContext(), "Unexpected response, check server file",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    public void endline_session_login(final String user_permission_id, final lines_model Line, final order_model PO, final String Lot, final bundle_model Bundle) {
    showLoader();
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("userID", user_id);
        params.put("lineID", Line.getLine_id());
        params.put("reworkState", Bundle.getRework_state());
        params.put("orderID", PO.getOrder_id());
        params.put("allowedModuleID", allowed_module_id);
        params.put("rejectedPieces", Bundle.getRejected_pieces());
        params.put("defectedPieces", Bundle.getFaulty_pieces());
        params.put("cutReportID", Bundle.getBundle_id());
        params.put("sectionID", section.section_id);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.endline_session, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("===="+response);
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");

                            if (error.equals("0")) {
                                hideLoader();
                                String session_id = response.getString("endLineSessionID");
                                Intent intent = new Intent(Bundle_Selection_Activity.this, Fault_Submission_Activity.class);
                                intent.putExtra("PO", PO);
                                intent.putExtra("Line", Line);
                                //intent.putExtra("Section", Section);
                                Bundle lists_bundle = new Bundle();
                                //lists_bundle.putSerializable("Departments_List", department_list);
                                lists_bundle.putSerializable("Faults_List", fault_list);
                                intent.putExtra("Lists", lists_bundle);
                                intent.putExtra("Lot", Lot);
                                intent.putExtra("Bundle", Bundle);
                                intent.putExtra("Session_id", session_id);
                                startActivity(intent);

                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to submit: " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), "Unexpected response, check server file",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}
