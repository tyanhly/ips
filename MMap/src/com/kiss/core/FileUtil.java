package com.kiss.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class FileUtil {

    public static boolean writeFile(Context c, String content, String filename) {
        try {
            FileOutputStream fos = c.openFileOutput(filename,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean appendFile(Context c, String content, String filename) {
        try {
          //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(c.getFilesDir() + "/" +filename, true)); 
            buf.append(content);
            buf.close();

            Log.i("FileLog", "appendFile-length: " + content.length());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readFile(Context context, String filename) {
        String tmp="";
        InputStream inputStream;
        try {
            inputStream = context.openFileInput(filename);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            final StringBuilder stringBuilder = new StringBuilder();
            
            boolean done = false;
            
            while (!done) {
                final String line = reader.readLine();
                done = (line == null);
                
                if (line != null) {
                    stringBuilder.append(line);
                }
            }
            
            reader.close();
            inputStream.close();

            tmp = stringBuilder.toString();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.i("FileLog", "readFile-length: " + tmp.length());
        return tmp;
    }
    

    public static void deleteFile(Context context, String filename) {
        File file = new File(context.getFilesDir(), filename);
        file.delete();
        

        Log.d("FileLog", "delete file:" + filename);
    }
}
