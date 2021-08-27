package com.example.endline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.endline.includes.Api_files;
import com.example.endline.includes.IP;
import com.example.endline.models.bundle_model;
import com.example.endline.models.fault_model;
import com.example.endline.models.lines_model;
import com.example.endline.models.operation_model;
import com.example.endline.models.order_model;
import com.example.endline.models.section_model;
import com.example.endline.models.size_model;
import com.google.gson.JsonObject;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fault_Submission_Activity extends AppCompatActivity {
    Button submitbtn;

    ProgressDialog nDialog;
    SearchableSpinner fault_spinner;
    SearchableSpinner operation_spinner;
    TextView text_operator;
    lines_model Lines_extra;
    order_model PO_extra;
    size_model Size_extra;
    String Lot_extra;
    bundle_model Bundle_extra;
    ArrayList<fault_model> fault_list = new ArrayList<>();
    ArrayList<fault_model> fault_list_backup = new ArrayList<>();
    ArrayList<fault_model> history_fault_list = new ArrayList<>();
    ArrayList<operation_model> operation_list = new ArrayList<>();
    IP ip;
    section_model section;
    NumberPicker fault_counter;
    Api_files api = new Api_files();
    LinearLayout fault_history_layout;
    TextView text_po;
    TextView text_style;
    TextView text_size;
    TextView text_lot;
    TextView text_color;
    TextView text_bundle;
    TextView text_bundleQuantity;
    boolean fault_submitted_flag = false;
    String Session_id;
    String is_quality_checked = "0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault__submission);
        Toolbar toolbar = findViewById(R.id.toolbar);

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String IP = sp.getString("IP", null);
        ip = new IP(IP);
        section = new section_model(sp.getString("sectionID", null), sp.getString("sectionCode", null));
        get_views();
        layout_listeners();
        get_extras();
        spinner_listeners();
        fetch_fault_category_list();

    }

    public void layout_listeners() {
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitbtn.setEnabled(false);
                if (fault_spinner.getSelectedItemPosition() > 0) {
                    if (operation_spinner.getSelectedItemPosition() > 0) {
                        if (fault_counter.getValue() > 0) {
                            fault_model fault = (fault_model) fault_spinner.getSelectedItem();
                            fault.setFault_count((String.valueOf(fault_counter.getValue())));
                            operation_model operation = (operation_model) operation_spinner.getSelectedItem();
                            submit_faults(fault, operation);
                        } else {
                            Toast.makeText(getApplicationContext(), "Fault Count Cannot Be Zero",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select operation!",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (!fault_submitted_flag) {
                        if (operation_list.size() > 1) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Fault_Submission_Activity.this);
                            alertDialog.setMessage("Are you sure this bundle is clear?")
                                    .setCancelable(false)
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Toast.makeText(getApplicationContext(), "Please provide defects",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            fault_model fault = new fault_model("1", "Clear", "Clear", "Clear", "0");
                                            fault.setFault_count("0");
                                            operation_model operation = (operation_model) operation_spinner.getItemAtPosition(1);
                                            submit_faults(fault, operation);
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Bundle can not be cleared! No scanning found for the bundle",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent openMainActivity = new Intent(Fault_Submission_Activity.this, Bundle_Selection_Activity.class);
                        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        openMainActivity.putExtra("Line", Lines_extra);
                        //openMainActivity.putExtra("Section", Section_extra);
                        openMainActivity.putExtra("PO", PO_extra);
                        openMainActivity.putExtra("Faults_List", fault_list_backup);
                        //openMainActivity.putExtra("Departments_List", department_list_backup);
                        startActivityIfNeeded(openMainActivity, 0);
                    }
                }
                submitbtn.setEnabled(true);
            }
        });
    }

    public void get_extras() {
        Intent i = getIntent();
        Lines_extra = (lines_model) i.getSerializableExtra("Line");
        Bundle_extra = (bundle_model) i.getSerializableExtra("Bundle");
        Size_extra =(size_model)i.getSerializableExtra("Size");
        Lot_extra = i.getStringExtra("Lot");
        PO_extra = (order_model) i.getSerializableExtra("PO");
        Bundle lists_bundle = i.getBundleExtra("Lists");
        fault_list = (ArrayList<fault_model>) lists_bundle.getSerializable("Faults_List");
        fault_list_backup = fault_list;
        Session_id = i.getStringExtra("Session_id");
        text_po.setText(PO_extra.getOrder_code());
        text_style.setText(PO_extra.getStyle());
        text_size.setText(Bundle_extra.getSize());
        text_color.setText(PO_extra.getColor());
        text_lot.setText(Lot_extra);
        text_bundle.setText(Bundle_extra.getBundle_code());
        if (Bundle_extra.getRework_state().equals("0"))
            text_bundleQuantity.setText(Bundle_extra.getBundle_qty());
        else if (Bundle_extra.getRework_state().equals("1")) {
            if(!Bundle_extra.getFaulty_pieces().equals("-1")){
                Bundle_extra.setBundle_qty(Bundle_extra.getFaulty_pieces());
            }
            text_bundleQuantity.setText(Bundle_extra.getBundle_qty());
        }
        if (text_bundleQuantity.getText().toString().equals("0")) {
            Toast.makeText(getApplicationContext(), "Bundle was cleared already!",
                    Toast.LENGTH_SHORT).show();
            Intent openMainActivity = new Intent(Fault_Submission_Activity.this, Bundle_Selection_Activity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            openMainActivity.putExtra("Line", Lines_extra);
            //openMainActivity.putExtra("Section", Section_extra);
            openMainActivity.putExtra("PO", PO_extra);
            openMainActivity.putExtra("Faults_List", fault_list_backup);
            //openMainActivity.putExtra("Departments_List", department_list_backup);
            startActivityIfNeeded(openMainActivity, 0);
        }
    }

    public void spinner_listeners() {
        fault_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (fault_spinner.getSelectedItemPosition() > 0) {
                    fault_counter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        operation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (operation_spinner.getSelectedItemPosition() <= 0) {
                    text_operator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showLoader() {
        nDialog = new ProgressDialog(Fault_Submission_Activity.this);
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

    public void get_views() {
        submitbtn = findViewById(R.id.submitbtn);
        fault_spinner = findViewById(R.id.fault_spinner);
        operation_spinner = findViewById(R.id.operation_spinner);
        text_operator = findViewById(R.id.text_operator);
        fault_counter = findViewById(R.id.number_picker);
        fault_history_layout = findViewById(R.id.fault_history_layout);
        text_style = findViewById(R.id.text_style);
        text_size = findViewById(R.id.text_size);
        text_lot = findViewById(R.id.text_cut);
        text_bundle = findViewById(R.id.text_bundle);
        text_bundleQuantity = findViewById(R.id.text_bundle_quantity);
        text_po = findViewById(R.id.text_po);
        text_color = findViewById(R.id.text_color);
    }

    public void fetch_fault_category_list(){
        showLoader();
        ArrayAdapter<fault_model> dataAdapter = new ArrayAdapter<>(Fault_Submission_Activity.this, android.R.layout.simple_spinner_item, fault_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fault_spinner.setAdapter(dataAdapter);
        hideLoader();
        fetch_operation();
    }

    public void submit_faults(final fault_model fault, final operation_model operation) {
        showLoader();
        final HashMap<String, String> params = new HashMap<>();
        params.put("endLineSessionID", Session_id);
        params.put("bundleScanID", operation.getBundle_scan_id());
        params.put("sectionID", section.section_id);
        JSONObject jsonResult = new JSONObject();
        try {
            jsonResult.accumulate("faultCount", fault.getFault_count());
            jsonResult.accumulate("faultID", fault.getFault_id());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("jsonResult->"+jsonResult);
        params.put("fault", jsonResult.toString());

        System.out.println(new JSONObject(params).toString());

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.registerFaults, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                hideLoader();
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Fault_Submission_Activity.this);
                                if (fault.getFault_id().equals("1")) {
                                    final AlertDialog dialog = dialogBuilder.create();
                                    is_quality_checked = "1";
                                    close_endline_session("0", "0", dialog);
                                    Toast.makeText(getApplicationContext(), "Bundle Cleared",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    dialogBuilder.setMessage("Do you want to add another fault?")
                                            .setCancelable(false)
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    show_rejection_dialog();
                                                }
                                            })
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    fault_spinner.setSelection(0);
                                                    fault_counter.setValue(1);
                                                    history_fault_list.add(new fault_model(fault.getFault_id(), fault.getFault_code(), fault.getFault_description(), "", fault.getFault_count()));
                                                    refresh_fault_history();
                                                    fault_counter.setVisibility(View.GONE);
                                                    fault_submitted_flag = true;
                                                }
                                            });
                                    AlertDialog alert = dialogBuilder.create();
                                    alert.show();
                                }
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

    public void refresh_fault_history() {
        fault_history_layout.removeAllViews();
        for (final fault_model fault : history_fault_list) {
            if (!fault.getFault_code().equals("null")) {
                View child = getLayoutInflater().inflate(R.layout.defect_single_row, null);
                final TextView text_fault_code = child.findViewById(R.id.itemid);
                final TextView text_fault_name = child.findViewById(R.id.itemtext);
                final TextView text_fault_count = child.findViewById(R.id.itemCount);
                text_fault_code.setText(fault.getFault_code());
                text_fault_name.setText(fault.getFault_description());
                text_fault_count.setText(fault.getFault_count());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10, 0, 10, 0);
                fault_history_layout.addView(child, layoutParams);
            }

        }
    }

    public void onBackPressed() {
        if (operation_list.size()>1){
            Toast.makeText(getApplicationContext(), "Can't go back",
                    Toast.LENGTH_SHORT).show();
        }
        else {
                    Intent openMainActivity = new Intent(Fault_Submission_Activity.this, Bundle_Selection_Activity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        openMainActivity.putExtra("Line", Lines_extra);
        //openMainActivity.putExtra("Section", Section_extra);
        openMainActivity.putExtra("PO", PO_extra);
        Bundle lists_bundle = new Bundle();
        //lists_bundle.putSerializable("Departments_List", department_list);
        lists_bundle.putSerializable("Faults_List", fault_list);
        openMainActivity.putExtra("Lists", lists_bundle);
        startActivityIfNeeded(openMainActivity, 0);
        }

    }

    public void fetch_operation() {
        showLoader();
        operation_list.clear();
        final HashMap<String, String> params = new HashMap<>();
        params.put("bundleID", Bundle_extra.getBundle_id());
        params.put("sectionID", section.section_id);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.getOperationWorkerForBundle, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                JSONArray s = response.getJSONArray("data");
                                System.out.println(s);
                                operation_list.add(new operation_model("0", "", "", "Please choose", "", "", ""));
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);

                                    String bundle_scan_id = res.getString("bundleScanID");
                                    String operation_id = res.getString("operationID");
                                    String operation_code = res.getString("operationCode");
                                    String operation_desc = res.getString("operationDescription");
                                    String worker_id =    res.getString("workerID");
                                    String worker_code = res.getString("workerCode");
                                    String worker_name = res.getString("workerDescription");
                                    operation_list.add(new operation_model(bundle_scan_id, operation_id, operation_code, operation_desc, worker_id, worker_code, worker_name));
                                }
                                if(s.length()!=0){
                                    ArrayAdapter<operation_model> dataAdapter = new ArrayAdapter<>(Fault_Submission_Activity.this, android.R.layout.simple_spinner_item, operation_list);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    operation_spinner.setAdapter(dataAdapter);
                                    set_operation_spinner();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "No operations available",
                                            Toast.LENGTH_SHORT).show();
                                }
                                hideLoader();

                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to fetch operations: " + desc,
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

    public void set_operation_spinner() {
        operation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (operation_spinner.getSelectedItemPosition() > 0) {
                    text_operator.setText("");
                    operation_model Operation = (operation_model) operation_spinner.getSelectedItem();
                    String operator = Operation.getWorker_code() + " " + Operation.getWorker_name();
                    text_operator.setText(operator);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void close_endline_session(final String rejected_pieces, final String fault_pieces, final AlertDialog dialog) {
        showLoader();
        final HashMap<String, String> params = new HashMap<>();
        params.put("endLineSessionID", Session_id);
        params.put("rejectedPieces", rejected_pieces);
        params.put("defectedPieces", fault_pieces);
        params.put("bundleID", Bundle_extra.getBundle_id());
        params.put("isQualityChecked", is_quality_checked);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.close_endline_session, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            System.out.println(response);
                            if (error.equals("0")) {
                                dialog.cancel();
                                hideLoader();
                                Intent openMainActivity = new Intent(Fault_Submission_Activity.this, Bundle_Selection_Activity.class);
                                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                openMainActivity.putExtra("Line", Lines_extra);
                                //openMainActivity.putExtra("Section", Section_extra);
                                openMainActivity.putExtra("PO", PO_extra);
                                openMainActivity.putExtra("Size", PO_extra);
                                openMainActivity.putExtra("Faults_List", fault_list_backup);
                                startActivityIfNeeded(openMainActivity, 0);
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

    public void show_rejection_dialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.endline_complete, null);
        dialogBuilder.setView(dialogView);
        final NumberPicker faulty_pieces = dialogView.findViewById(R.id.picker_faulty_pieces);
        final NumberPicker rejected_pieces = dialogView.findViewById(R.id.picker_rejected_pieces);
        faulty_pieces.setMin(1);
        if(!Bundle_extra.getFaulty_pieces().equals("-1")){
            faulty_pieces.setValue(Integer.parseInt(Bundle_extra.getFaulty_pieces()));
        }
        if(!Bundle_extra.getFaulty_pieces().equals("-1")) {
            rejected_pieces.setValue(Integer.parseInt(Bundle_extra.getRejected_pieces()));
        }
        Button submit_endline_session = dialogView.findViewById(R.id.submit_endline_sesion);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        submit_endline_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faulty_pieces.getValue() == 0) {
                    is_quality_checked = "1";
                } else {
                    is_quality_checked = "0";
                }


                System.out.println(faulty_pieces.getValue()+"==="+rejected_pieces.getValue()+"==="+Integer.parseInt(Bundle_extra.getBundle_qty()));
                if ((faulty_pieces.getValue() > Integer.parseInt(Bundle_extra.getBundle_qty()))
                        || (rejected_pieces.getValue() > Integer.parseInt(Bundle_extra.getBundle_qty()))
                        || (faulty_pieces.getValue() + rejected_pieces.getValue() > Integer.parseInt(Bundle_extra.getBundle_qty()))) {
                    Toast.makeText(getApplicationContext(), "Invalid Values! Faulty/Rejected Pieces can not be more than Bundle Quantity",
                            Toast.LENGTH_SHORT).show();
                } else {
                    close_endline_session(String.valueOf(rejected_pieces.getValue()), String.valueOf(faulty_pieces.getValue()), alertDialog);
                }

            }
        });

    }
}
