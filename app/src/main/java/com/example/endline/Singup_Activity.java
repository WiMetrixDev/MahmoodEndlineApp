package com.example.endline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.endline.models.activity_model;
import com.example.endline.models.module_model;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Singup_Activity extends AppCompatActivity {
    EditText text_username;
    EditText text_password;
    Button btn_sign_in;
    Button btn_set_ip;
    ProgressDialog nDialog;
    TextView app_ver;
    IP ip;
    ArrayList<activity_model> activity_list = new ArrayList<>();
    Api_files api = new Api_files();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        if(sp.contains("username")){
            Intent intent = new Intent(Singup_Activity.this, Line_Activity.class);
            startActivity(intent);
        }
        String IP = sp.getString("IP", null);
        ip = new IP(IP);
        layout_views();
        layout_listeners();
        get_extras();
    }

    public void layout_views() {
        text_username = findViewById(R.id.username);
        text_password = findViewById(R.id.password);
        btn_sign_in = findViewById(R.id.signinbtn);
        btn_set_ip = findViewById(R.id.setIp);
        app_ver  =findViewById(R.id.app_ver);
        app_ver.setText("V "+BuildConfig.VERSION_NAME);
    }

    public void get_extras() {
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        text_username.setText(username);
        text_password.setText(password);
    }

    public void showLoader() {
        nDialog = new ProgressDialog(Singup_Activity.this);
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


    public void layout_listeners() {
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text_username.getText().toString().equals("") && !text_password.getText().toString().equals("")) {
                    verify_and_login(text_username.getText().toString(), text_password.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Provide Username and password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_set_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Singup_Activity.this);
                builder.setTitle("Please provide IP");
                final EditText input = new EditText(Singup_Activity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("IP", "http://" + m_Text);
                        ip = new IP("http://" + m_Text);
                        editor.apply();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    public void verify_and_login(final String username, final String password) {
        showLoader();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", username);
        params.put("password", password);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.user_login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            hideLoader();
                            String error = response.getString("errorNo");
                            String desc = response.getString("errorDescription");
                            if (error.equals("0")) {
                                JSONArray s = response.getJSONArray("modules");
                                for (int i = 0; i < s.length(); i++) {
                                    JSONObject res = (JSONObject) s.get(i);
                                    String ModuleName = res.getString("moduleName");
                                    System.out.println(ModuleName);
                                    if(ModuleName.equalsIgnoreCase("ENDLINE")){
                                        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(activity_list);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("username", username);
                                        editor.putString("password", password);
                                        editor.putString("userID", res.getString("userID"));
                                        editor.putString("allowedModuleID", res.getString("allowedModuleID"));
                                        editor.putString("ModuleName", res.getString("moduleName"));
                                        editor.apply();
                                        Intent intent = new Intent(Singup_Activity.this, Line_Activity.class);
                                        intent.putExtra("Activity",new module_model(res.getString("moduleName"),res.getString("allowedModuleID")));
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                hideLoader();
                                Toast.makeText(getApplicationContext(), "Unable to Login " + desc,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            e.printStackTrace();
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
}
