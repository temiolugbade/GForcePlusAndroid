package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Experiment {
    private static final String TAG = "Experiment";
    int id;
    int p_id;
    int phone_id;
    int armband_id_l;
    int armband_id_r;


    public Experiment(int p_id, int phone_id, int armband_id_l, int armband_id_r) {
        this.p_id = p_id;
        this.phone_id = phone_id;
        this.armband_id_l = armband_id_l;
        this.armband_id_r = armband_id_r;
    }


    public int insertExperiment(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("p_id", p_id);
        values.put("phone_id", phone_id);
        values.put("armband_id_l", armband_id_l);
        values.put("armband_id_r", armband_id_r);
        values.put("state", 0);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Experiment", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            Log.e(TAG, e.getMessage());
            return -1;
        }

    }
    public static void updateExpProgress(SQLiteDatabase db, int id, int state) {
        ContentValues values = new ContentValues();
        values.put("state", state);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
        db.update("Experiment", values, "id=?", new String[]{String.valueOf(id)});
        values.clear();
    }

    public boolean updateExperiment() {

        return true;
    }
    public boolean deleteExperiment() {

        return true;
    }
    public int getState() {

        return 99;
    }
    public class State {
        public static final int START = 0;
        public static final int FINISHED = 1;
    }
}
