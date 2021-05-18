package com.example.endline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.endline.Reader.NFCReaderService;
import com.example.endline.includes.Api_files;
import com.example.endline.includes.IP;
import com.example.endline.models.fault_model;
import com.example.endline.models.lines_model;
import com.example.endline.models.module_model;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Line_Activity extends AppCompatActivity {
    ArrayList<lines_model> line_list = new ArrayList<>();
    ArrayList<fault_model> fault_list = new ArrayList<>();
    ProgressDialog nDialog;
    SearchableSpinner lines_spinner;
    Button submitbtn;
    Button logoutbtn;
    IP ip;
    Api_files api = new Api_files();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String IP = sp.getString("IP", null);
        ip = new IP(IP);
        layout_views();
        layout_listeners();
        layout_spinners();
        fetch_lines();
    }

    public void layout_views() {
        submitbtn = findViewById(R.id.submitbtn);
        lines_spinner = findViewById(R.id.spinner_line);
        logoutbtn = findViewById(R.id.logoutbtn);
    }

    public void layout_listeners() {
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lines_spinner.getSelectedItemPosition() > 0) {
                    fetch_fault_category_list();
                } else {
                    Toast.makeText(getApplicationContext(), "Incomplete Form!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }


    public void logOut(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        prefs.edit().remove("username").commit();
        prefs.edit().remove("password").commit();
        Intent intent = new Intent(Line_Activity.this, Singup_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showLoader() {
        nDialog = new ProgressDialog(Line_Activity.this);
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

    public void fetch_lines() {
        showLoader();
        HashMap<String, String> params = new HashMap<String, String>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, ip.getIp() +api.get_all_lines, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            line_list.add(new lines_model("", "Please choose"));
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            System.out.println("error " + error);
                            if (error.equals("0")) {
                                JSONArray s = response.getJSONArray("data");
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);
                                    String line_id = res.getString("lineID");
                                    String line_code = res.getString("lineCode");
                                    line_list.add(new lines_model(line_id, line_code));
                                }
                                ArrayAdapter<lines_model> dataAdapter = new ArrayAdapter<>(Line_Activity.this, android.R.layout.simple_spinner_item, line_list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                lines_spinner.setAdapter(dataAdapter);
                                hideLoader();
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to fetch Lines " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            hideLoader();
                            Toast.makeText(getApplicationContext(), "Unexpected Response from Server while fetching Lines",
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

    public void fetch_fault_category_list() {
        showLoader();
        fault_list.clear();
        HashMap<String, String> params = new HashMap<String, String>();
        final ArrayList<String> faultTrack = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, ip.getIp() + api.get_faults, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                System.out.println(response);
                                JSONArray s = response.getJSONArray("data");
                                fault_list.add(new fault_model("", "", "Please Choose", "", ""));
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);
                                    String fault_id = res.getString("faultID");
                                    String fault_code = res.getString("faultCode");
                                    String fault_description = res.getString("faultDescription");
                                    if (!faultTrack.contains(fault_id)) {
                                        faultTrack.add(fault_id);
                                        fault_list.add(new fault_model(fault_id, fault_code, fault_description, "", "0"));
                                    }
                                }
                                Intent intent = new Intent(Line_Activity.this, Bundle_Selection_Activity.class);
                                Bundle lists_bundle = new Bundle();
                                lists_bundle.putSerializable("Faults_List", fault_list);
                                intent.putExtra("Lists", lists_bundle);
                                lines_model Line = (lines_model) lines_spinner.getSelectedItem();
                                intent.putExtra("Line", Line);
                                startActivity(intent);
                                hideLoader();
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to Fetch Faults " + desc,
                                        Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), "Unexpected Response from Server",
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

    public void layout_spinners() {
        lines_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Can not go back!",
                Toast.LENGTH_SHORT).show();
    }
}
