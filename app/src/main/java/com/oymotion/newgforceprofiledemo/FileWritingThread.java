package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
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

        try {
            myWriter.write(create_header());
            myWriter.newLine();
        } catch (IOException e) {
        Log.d("EXPECTED", "Something went wrong in writing data to file");
        }

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

                    write_basic(tempStore);
                    reset();


                }

                for (String data_keys : DatabaseUtil.DATA_KEYS_LIST) {

                    checkData.put(type, true);
                    if(data.containsKey(data_keys)) {

                        String key = hand+data_keys;
                        String val = data.getAsString(data_keys);


                        if (type.endsWith("EMG") && data_keys.startsWith("ch")){



                            val = val.substring(1, val.length()-1);



                        }


                        tempStore.put(key, val);
                        }


                    }



            }



        }


    }



    private void write_basic(Map<String, String> data){

        StringBuilder s = new StringBuilder();



        for (String hand : hands) {

            s.append(DatabaseUtil.getDateTime());

            for (String data_key : DatabaseUtil.DATA_KEYS_LIST) {

                s.append(",");

                if (data.containsKey(hand+data_key)) {


                     s.append(data.get(hand+data_key));

                } else {

                    if (data_key.startsWith("ch")){


                        for(int i=1; i<16; i++) {
                            s.append("NA");
                            s.append(",");
                        }
                        s.append("NA");


                    }else {
                        s.append("NA");
                    }

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

        tempStore.clear();


    }

    private String create_header(){

        StringBuilder s = new StringBuilder();

        s.append("time_written_to_file");

        for (String hand : hands) {

            for (String data_key : DatabaseUtil.DATA_KEYS_LIST) {

                s.append(",");

                switch(data_key){

                    case "p_id":

                        s.append("participant_id");

                        break;

                    case "prj_id":

                        s.append("project_id");

                        break;

                    case "itr_id":

                        s.append("interaction_id");

                        break;

                    case "itr_type":

                        s.append("interaction_type");

                        break;

                    case "hand":

                        s.append("hand");

                        break;

                    case "clt_id":

                        s.append("material_id");

                        break;

                    case "timestamp":

                        s.append(hand+"_time_of_last_sensor_sampled");

                        break;

                    case "w":

                        s.append(hand+"_quaternion_w");

                        break;

                    case "x":

                        s.append(hand+"_quaternion_x");

                        break;

                    case "y":

                        s.append(hand+"_quaternion_y");

                        break;

                    case "z":

                        s.append(hand+"_quaternion_z");

                        break;

                    case "pitch":

                        s.append(hand+"_pitch");

                        break;

                    case "roll":

                        s.append(hand+"_roll");

                        break;

                    case "yaw":

                        s.append(hand+"_yaw");

                        break;

                    case "gyr_x":

                        s.append(hand+"_gyroscope_x");

                        break;

                    case "gyr_y":

                        s.append(hand+"_gyroscope_y");

                        break;

                    case "gyr_z":

                        s.append(hand+"_gyroscope_z");

                        break;

                    case "acc_x":

                        s.append(hand+"_accelerometer_x");

                        break;

                    case "acc_y":

                        s.append(hand+"_accelerometer_y");

                        break;

                    case "acc_z":

                        s.append(hand+"_accelerometer_z");

                        break;

                    case "mag_x":

                        s.append(hand+"_magnetometer_x");

                        break;

                    case "mag_y":

                        s.append(hand+"_magnetometer_y");

                        break;

                    case "mag_z":

                        s.append(hand+"_magnetometer_z");

                        break;

                    case "ch_01":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel01_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel01_time"+16);
                        break;

                    case "ch_02":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel02_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel02_time"+16);
                        break;

                    case "ch_03":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel03_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel03_time"+16);
                        break;

                    case "ch_04":

                        for(int i=1; i<16; i++) {
                        s.append(hand+"_emg_channel04_time"+i);
                        s.append(",");

                    }
                    s.append(hand+"_emg_channel04_time"+16);
                        break;

                    case "ch_05":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel05_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel05_time"+16);
                        break;

                    case "ch_06":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel06_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel06_time"+16);
                        break;

                    case "ch_07":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel07_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel07_time"+16);
                        break;

                    case "ch_08":

                        for(int i=1; i<16; i++) {
                            s.append(hand+"_emg_channel08_time"+i);
                            s.append(",");

                        }
                        s.append(hand+"_emg_channel08_time"+16);
                        break;




                }



            }

        }

        return s.toString();

    }


}
