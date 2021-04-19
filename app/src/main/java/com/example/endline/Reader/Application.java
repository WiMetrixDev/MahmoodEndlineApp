package com.example.endline.Reader;

import com.rfid.reader.Reader;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Administrator on 2017-6-22.
 */
public class Application extends android.app.Application {
    public Reader reader = null;

    public Reader getReader() throws SecurityException, IOException, InvalidParameterException {
        if (reader == null) {
	        String path = "/dev/ttyS0";
            reader = Reader.getInstance(path, 9600);
        }
        return reader;
    }

    public void close() {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}