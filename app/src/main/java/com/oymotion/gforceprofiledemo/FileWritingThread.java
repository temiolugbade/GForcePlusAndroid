package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileWritingThread extends HandlerThread {

    private static final String TAG = "FileWritingClass";

    private Handler myHandler;
    private BufferedWriter myWriter;
    private String storageDirectoryName = "TCC_GFORCE_OYMOTION";

    private Map<String, Boolean> checkData = new HashMap<>();

    private Map<String, String> tempStore = new HashMap<>();

    List<String> hands = Arrays.asList("left", "right");




    public FileWritingThread(String name){

        super(name);
        myHandler = new Handler(Looper.myLooper());

        createWriteFolder();

        reset();


    }

    public Handler getHandler(){
        return myHandler;
    }


    private void createWriteFolder(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //fail safely
            Log.i(TAG, "I CANNOT write to storage");
        }else{

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    storageDirectoryName);

            if (!dir.mkdirs()) {
                if(!dir.exists()) {
                    Log.i(TAG, "Could NOT create directory");
                }
            }

            File file = new File(dir, storageDirectoryName+" "+DatabaseUtil.getDateTime()+".txt");
            try {
                myWriter = new BufferedWriter(new FileWriter(file));
            }catch(IOException e){
                Log.i(TAG, "Writer NOT created");
            }



        }

    }

    public void writeDataToFile(ContentValues data) {

        String hand = "none";


        if (data.getAsInteger("hand") == 0){

            hand = "left";

        } else if (data.getAsInteger("hand") == 1){

            hand = "right";

        }

        String data_type = hand+data.get("data_type");



        for (String type : checkData.keySet()) {


            if (data_type.equals(type)) {



                if (checkData.get(type)) {

                    reset();
                    write_basic(tempStore);

                } else {

                    for (String data_keys : DatabaseUtil.DATA_KEYS_LIST) {

                        checkData.put(type, true);
                        if(data.containsKey(data_keys)) {

                            String key = hand+data_keys;
                            String val = data.getAsString(data_keys);

                            /**
                            if (type.endsWith("EMG")){

                                System.out.println("XXX --- "+type);
                                System.out.println("XXX "+val);

                                val = val.substring(1, val.length()-1);

                            }
                             **/

                            tempStore.put(key, val);
                        }


                    }

                }

            }



        }


    }



    private void write_basic(Map<String, String> data){

        StringBuilder s = new StringBuilder();

        s.append(DatabaseUtil.getDateTime());

        for (String hand : hands) {

            for (String data_key : DatabaseUtil.DATA_KEYS_LIST) {

                s.append(",");

                if (data.containsKey(hand+data_key)) {


                     s.append(data.get(hand+data_key));

                } else {

                    s.append("NA");

                }


            }

        }

        try {

            myWriter.write(s.toString());
            myWriter.newLine();

            Log.d("EXPECTED", "Handler has posted new writing task to writing thread");


        } catch (IOException e) {
            Log.d("EXPECTED", "Something went wrong in writing data to file");
        }



    }


    private void reset(){

        for (String hand : hands) {
            for (String data_type : DatabaseUtil.DATA_TYPES) {

                checkData.put(hand+data_type, false);

            }
        }


    }


}
