package com.example.endline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.endline.includes.Api_files;
import com.example.endline.includes.IP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Splash_Screen_Activity extends AppCompatActivity {
    Handler handler;
    IP ip = null;
    String finalPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        handler = new Handler();
        checkVersion();
        }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(Splash_Screen_Activity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Splash_Screen_Activity.this, new String[] { permission }, requestCode);
        }
        else {
            UpdateApp atualizaApp = new UpdateApp(this);
            atualizaApp.setContext(getApplicationContext());
            atualizaApp.execute(finalPath);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    UpdateApp atualizaApp = new UpdateApp(this);
                    atualizaApp.setContext(getApplicationContext());
                    atualizaApp.execute(finalPath);

                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    public void checkVersion() {
        System.out.println("checking version");
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String IP = sp.getString("IP", null);
        ip = new IP(IP);
        if (IP != null) {
            Api_files api = new Api_files();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("appType","Endline");
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ip.getIp() + api.get_latest_version, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String error = response.getString("errorNo");
                                String desc = response.getString("errorDescription");
                                String VersionName = null;
                                String Path = null;
                                if (error.equals("0")) {
                                    JSONObject s = response.getJSONObject("data");
                                    VersionName = s.getString("version");
                                    Path = s.getString("address");

                                    if (!VersionName.equals(BuildConfig.VERSION_NAME)) {
                                        finalPath = Path;
                                        AlertDialog alertDialog = new AlertDialog.Builder(Splash_Screen_Activity.this)
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .setTitle("New Version Detected")
                                                .setMessage("New updated version " + VersionName + " available! App needs to be update.")
                                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //set what would happen when positive button is clicked
                                                        //downloading new version
                                                        checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", 11);

                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    } else {
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(Splash_Screen_Activity.this, Singup_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }, 3000);
                                    }

                                    //

                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to Fetch Version: " + desc,
                                            Toast.LENGTH_SHORT).show();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(Splash_Screen_Activity.this, Singup_Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 3000);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Unexpected Response from Server",
                                        Toast.LENGTH_SHORT).show();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(Splash_Screen_Activity.this, Singup_Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 3000);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(),
                            Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Splash_Screen_Activity.this, Singup_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                }
            });
            queue.add(req);
        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash_Screen_Activity.this, Singup_Activity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }
    }



}
