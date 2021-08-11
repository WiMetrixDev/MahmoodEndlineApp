package com.example.endline.Reader;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.endline.utils.Converter;
import com.rfid.reader.Reader;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NFCReaderService extends Service {
    private Reader reader;
    private boolean isRunning = true;
    private String message = "";
    private Application app = new Application();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("time--------", "Comming here");
        startReaderThread();

    }
    private void startReaderThread() {
        try {
            reader = app.getReader();
            if (reader == null) {
                Toast.makeText(this, "\n" +
                        "There is no serial port available for this device, please close the software", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        new NFCReaderService.ReadThread().start();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            }
        }
    };
    public class ReadThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                try {

                byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
                byte[] rData = new byte[48];
                byte[] errCode = new byte[1];
                long startMs = System.currentTimeMillis();
                int result = reader.Iso14443a_Read((byte) 0x05, (byte)0x03, (byte) 0x00, key, rData, errCode);
                if (result == 0){
                    StringBuilder strData = new StringBuilder();
                    for (int i = 0; i < 16; i++) {
                        strData.append(String.format("%02X ", rData[i]));
                    }
                    int card_id = ((0xFF & rData[3]) << 24) | ((0xFF & rData[2]) << 16) | ((0xFF & rData[1]) << 8) | ((0xFF & rData[0]));
                    String readData = Converter.byteArrayToHexString(rData);
                    int card_type = Integer.parseInt(readData.substring(9,10));
                    if(card_type == 0 ){
                        card_id = Integer.parseInt(readData.substring(0, 8));
                    }
                    Intent broadCastIntent = new Intent("nfc.tag");
                    broadCastIntent.putExtra("TagNumber", card_id);
                    broadCastIntent.putExtra("TagType", card_type);
                    sendBroadcast(broadCastIntent);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
