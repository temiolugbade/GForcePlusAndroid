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

    private String storageDirectoryName = "TCC_GFORCE_OYMOTION";


    private String myBegintimestamp;



    private List<String> hands = Arrays.asList("left", "right");

    private Map<String, BufferedWriter> myWriters = new HashMap<String, BufferedWriter>();


    private List<String> QUATERNION_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
            "clt_id", "timestamp", "w", "x", "y", "z");

    private List<String> EMG_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
            "clt_id", "timestamp", "ch_01", "ch_02", "ch_03", "ch_04", "ch_05", "ch_06", "ch_07", "ch_08");

    private List<String> EULERANGLES_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
            "clt_id", "timestamp", "pitch", "roll", "yaw");

    private List<String> GYROSCOPE_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
            "clt_id", "timestamp", "gyr_x", "gyr_y", "gyr_z");

    private List<String> MAGNETOMETER_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
            "clt_id", "timestamp", "mag_x", "mag_y", "mag_z");

    private List<String> ACCELEROMETER_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
            "clt_id", "timestamp", "acc_x", "acc_y", "acc_z");

    private File datafolder;



    private String experience_filename = storageDirectoryName + " experience.txt";



    public FileWritingThread(String begintimestamp){

        super(begintimestamp);
        myHandler = new Handler(Looper.myLooper());

        myBegintimestamp = begintimestamp;

        datafolder = createWriteFilesBasic();




    }

    public Handler getHandler(){
        return myHandler;
    }

    public File createWriteFilesBasic(){

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //fail safely
            Log.i(TAG, "I CANNOT write to storage");

            return null;
        }else {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    storageDirectoryName);

            if (!dir.mkdirs()) {
                if (!dir.exists()) {
                    Log.i(TAG, "Could NOT create directory");
                }
            }


            File thisdatafolder = new File(dir, storageDirectoryName + " " + myBegintimestamp);

            if (!thisdatafolder.mkdirs()) {
                if (!dir.exists()) {
                    Log.i(TAG, "Could NOT create data folder");
                }
            }

            return thisdatafolder;

        }
    }


    public void createWriteExperienceFiles(){

        File experience_file = new File(datafolder, experience_filename);

        try {
            BufferedWriter myExperienceWriter = new BufferedWriter(new FileWriter(experience_file));
            myExperienceWriter.write(create_header_experience());
            myExperienceWriter.close();
        } catch (IOException e) {

            Log.i(TAG, "Writer NOT created for experience");
        }



    }

    public void createWriteSensorFiles(){


        for (String hand : hands) {
            for (String data_type : DatabaseUtil.DATA_TYPES) {

                File file = new File(datafolder,
                        storageDirectoryName + " " + hand + " " +data_type + ".txt");
                try {
                    BufferedWriter myWriter = new BufferedWriter(new FileWriter(file));
                    myWriter.write(create_header(data_type));
                    myWriter.newLine();
                    myWriters.put(hand+data_type, myWriter);
                } catch (IOException e) {

                    Log.i(TAG, "Writer NOT created for "+data_type);
                }
            }

        }



    }

    public void writeExperienceToFile(List<String> experience_data) {

        File experience_file = new File(datafolder, experience_filename);

        StringBuilder s = new StringBuilder();
        s.append(DatabaseUtil.getDateTime());

        for(String val : experience_data) {

            s.append(",");
            s.append(val);

        }

        try {

            BufferedWriter myExperienceWriter = new BufferedWriter(new FileWriter(experience_file, true));
            myExperienceWriter.newLine();
            myExperienceWriter.write(s.toString());

            myExperienceWriter.close();

            Log.d("EXPECTED", "Experience added to file");


        } catch (IOException e) {
            Log.d("EXPECTED", "Something went wrong in writing data to file");
        }

    }

    public void writeDataToFile(ContentValues data) {

        String hand = "none";


        if (data.getAsInteger("hand") == 0) {

            hand = "left";

        } else if (data.getAsInteger("hand") == 1) {

            hand = "right";

        }

        String data_type = data.getAsString("data_type");

        Map<String, String> tempStore = new HashMap<>();


        for (String data_key : DatabaseUtil.DATA_KEYS_LIST) {


            if (data.containsKey(data_key)) {

                String val = data.getAsString(data_key);


                if (data_type.endsWith("EMG") && data_key.startsWith("ch")) {


                    val = val.substring(1, val.length() - 1);


                }


                tempStore.put(data_key, val);
            }


        }


        write_basic(tempStore, myWriters.get(hand+data_type), get_keyslist(data_type));




    }



    private void write_basic(Map<String, String> data, BufferedWriter myWriter, List<String> keyslist){

        StringBuilder s = new StringBuilder();

        s.append(DatabaseUtil.getDateTime());


        for (String data_key : keyslist) {

            s.append(",");

            if (data.containsKey(data_key)) {


                 s.append(data.get(data_key));

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



        try {

            myWriter.write(s.toString());
            myWriter.newLine();

            Log.d("EXPECTED", "Handler has posted new writing task to writing thread");


        } catch (IOException e) {
            Log.d("EXPECTED", "Something went wrong in writing data to file");
        }



    }


    private String create_header_experience(){

        StringBuilder s = new StringBuilder();

        s.append("timestamp");
        s.append(",");
        s.append("participant_id");
        s.append(",");
        s.append("project_id");
        s.append(",");
        s.append("interaction_id");
        s.append(",");
        s.append("interaction_type");
        s.append(",");
        s.append("material_id");
        s.append(",");
        s.append("property_id");
        s.append(",");
        s.append("experience");



        return s.toString();


    }



    private String create_header(String data_type){

        StringBuilder s = new StringBuilder();

        s.append("write_timestamp");



        for (String data_key : get_keyslist(data_type)) {

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

                    s.append("signal_timestamp");

                    break;

                case "w":

                    s.append("quaternion_w");

                    break;

                case "x":

                    s.append("quaternion_x");

                    break;

                case "y":

                    s.append("quaternion_y");

                    break;

                case "z":

                    s.append("quaternion_z");

                    break;

                case "pitch":

                    s.append("pitch");

                    break;

                case "roll":

                    s.append("roll");

                    break;

                case "yaw":

                    s.append("yaw");

                    break;

                case "gyr_x":

                    s.append("gyroscope_x");

                    break;

                case "gyr_y":

                    s.append("gyroscope_y");

                    break;

                case "gyr_z":

                    s.append("gyroscope_z");

                    break;

                case "acc_x":

                    s.append("accelerometer_x");

                    break;

                case "acc_y":

                    s.append("accelerometer_y");

                    break;

                case "acc_z":

                    s.append("accelerometer_z");

                    break;

                case "mag_x":

                    s.append("magnetometer_x");

                    break;

                case "mag_y":

                    s.append("magnetometer_y");

                    break;

                case "mag_z":

                    s.append("magnetometer_z");

                    break;

                case "ch_01":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel01_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel01_time"+16);
                    break;

                case "ch_02":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel02_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel02_time"+16);
                    break;

                case "ch_03":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel03_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel03_time"+16);
                    break;

                case "ch_04":

                    for(int i=1; i<16; i++) {
                    s.append("emg_channel04_time"+i);
                    s.append(",");

                }
                s.append("emg_channel04_time"+16);
                    break;

                case "ch_05":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel05_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel05_time"+16);
                    break;

                case "ch_06":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel06_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel06_time"+16);
                    break;

                case "ch_07":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel07_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel07_time"+16);
                    break;

                case "ch_08":

                    for(int i=1; i<16; i++) {
                        s.append("emg_channel08_time"+i);
                        s.append(",");

                    }
                    s.append("emg_channel08_time"+16);
                    break;




            }



        }



        return s.toString();

    }


    private List<String> get_keyslist(String data_type){

        List<String> list = Arrays.asList();

        switch(data_type){
            case "quaternion":

                list = QUATERNION_KEYS_LIST;

                break;

            case "EMG":

                list = EMG_KEYS_LIST;

                break;

            case "EulerAngles":

                list = EULERANGLES_KEYS_LIST;

                break;

            case "gyroscope":

                list = GYROSCOPE_KEYS_LIST;

                break;

            case "magnetometer":

                list = MAGNETOMETER_KEYS_LIST;

                break;


            case "accelerometer":


                list = ACCELEROMETER_KEYS_LIST;

                break;

        }


        return list;


    }




}
