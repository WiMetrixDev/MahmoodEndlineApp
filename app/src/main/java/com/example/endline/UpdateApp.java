package com.example.endline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateApp extends AsyncTask<String,Integer ,Void> {
    private Context context;
    private File outputFile;
    File file;
    private ProgressDialog dialog;


    public UpdateApp(Splash_Screen_Activity activity) {
        dialog = new ProgressDialog(activity);
    }

    public void setContext(Context contextf){
        context = contextf;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Downloading app please wait.");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();


            String destination = Environment.getExternalStorageDirectory() + "/download/";
            file = new File(destination);
            file.mkdirs();
            outputFile = new File(file, "MahmoodEndline.apk");
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        Uri fileUri=FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider",outputFile );

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);

        } else{
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = "MahmoodEndline.apk";
            destination += fileName;
            Uri uri = Uri.parse("file://" + destination);

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(uri,"application/vnd.android.package-archive");
            context.startActivity(install);
        }
    }

}