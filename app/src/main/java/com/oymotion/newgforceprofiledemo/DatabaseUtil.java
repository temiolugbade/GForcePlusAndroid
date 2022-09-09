package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";
//
//    private GForceDatabaseOpenHelper dbOpenHelper = new GForceDatabaseOpenHelper(this, "GForce.db",null,1);
//    private static SQLiteDatabase db = dbOpenHelper.getWritableDatabase();


    public final static List<String> DATA_KEYS_LIST = Arrays.asList("p_id", "prj_id", "itr_id", "itr_type", "hand",
        "clt_id", "timestamp", "w", "x", "y", "z", "pitch", "roll", "yaw", "gyr_x", "gyr_y", "gyr_z",
            "acc_x", "acc_y", "acc_z", "mag_x", "mag_y", "mag_z",
            "ch_01", "ch_02", "ch_03", "ch_04", "ch_05", "ch_06", "ch_07", "ch_08");



    public final static List<String> DATA_TYPES = Arrays.asList("quaternion", "EMG", "EulerAngles", "gyroscope", "magnetometer", "accelerometer");






    public static String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = sdf.format(new Date());
        return timestamp;
    }

    public static String getDateTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS");
        return simpleDateFormat.format(calendar.getTime()).toString();
    }


    public static boolean InsertParticipant(SQLiteDatabase db, int p_id){
       Cursor cursor = db.query("Participant",new String[]{"p_id"},null,null,null,null,null,null);
       while (cursor.moveToNext()){
           int id = cursor.getInt(cursor.getColumnIndex("p_id"));
           if(p_id == id){
               return true;//add update code to update gender
           }
       }
        ContentValues values = new ContentValues();
        values.put("p_id", p_id);
//        values.put("gender", gender);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            db.insert("Participant", null, values);
            values.clear();
            return true;
        }catch (Exception e){
            values.clear();
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public static boolean insertAcc(SQLiteDatabase db, int p_id, String gender){
        ContentValues values = new ContentValues();
        values.put("p_id", p_id);
        values.put("gender", gender);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            db.insert("Participant", null, values);
            values.clear();
            return true;
        }catch (Exception e){
            values.clear();
            Log.e(TAG, e.getMessage());
            return false;
        }

    }
}
