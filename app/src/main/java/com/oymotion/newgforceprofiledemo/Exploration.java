package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 04/12/2021
 * Time: 18:26
 * Description:
 */
public class Exploration {

    private static final String TAG = "Exploration";
    int id;
    int prj_id;
    int p_id;
    int clt_id;
    int ppt_id;
    String ppt_name;
    int rating;
    int itr_id;
    int state = 0;


    ContentValues values = new ContentValues();


    public Exploration(int prj_id, int p_id, int clt_id, int ppt_id, String ppt_name) {
        this.prj_id = prj_id;
        this.p_id = p_id;
        this.clt_id = clt_id;
        this.ppt_id = ppt_id;
        this.ppt_name = ppt_name;
    }

    public Exploration(int id, int prj_id, int p_id, int clt_id, int ppt_id, String ppt_name, int rating, int itr_id, int state) {
        this.id = id;
        this.prj_id = prj_id;
        this.p_id = p_id;
        this.clt_id = clt_id;
        this.ppt_id = ppt_id;
        this.ppt_name = ppt_name;
        this.rating = rating;
        this.itr_id = itr_id;
        this.state = state;
    }


    public int getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public int getState() {

        return state;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getPrj_id() {
        return prj_id;
    }

    public int getP_id() {
        return p_id;
    }

    public int getClt_id() {
        return clt_id;
    }

    public int getPpt_id() {
        return ppt_id;
    }

    public String getPpt_name() {
        return ppt_name;
    }

    public int getItr_id() {
        return itr_id;
    }

    public ContentValues getValues() {
        return values;
    }

    static ArrayList<Exploration> getExplorationList(SQLiteDatabase db, int clt_id) {
        ArrayList<Exploration> explorationList = new ArrayList<>();
        try {
            Cursor result = db.query("Exploration",null,"clt_id = ? and state != ?",new String[]{String.valueOf(clt_id),String.valueOf(Project.State.DELETED)},null,null,null);
            Log.i(TAG, "getExplorationList: "+result);
            while (result.moveToNext()){
                Log.i(TAG, "getExplorationList: "+result.getColumnName(1));
                int id = result.getInt(0);
                int prj_id = result.getInt(1);
                int p_id = result.getInt(2);
                int mclt_id = result.getInt(3);
                int ppt_id = result.getInt(4);
                String ppt_name = result.getString(5);
                int rating = result.getInt(6);
                int itr_id = result.getInt(7);
                int state = result.getInt(8);
                Exploration exploration = new Exploration(id,prj_id,p_id,mclt_id,ppt_id,ppt_name,rating,itr_id,state);
                explorationList.add(exploration);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return explorationList;
    }


    public int insertExploration(SQLiteDatabase db) {
        Log.i(TAG, "insertExploration: ");

        values.put("prj_id",prj_id);
        values.put("p_id", p_id);
        values.put("clt_id", clt_id);
        values.put("ppt_id", ppt_id);
        values.put("ppt_name", ppt_name);
        values.put("state", State.INCOMPLETE);
        values.put("timestamp", DatabaseUtil.getTimestamp());

        try {
            id = (int) db.insert("Exploration", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            Log.e(TAG, e.getMessage());
            return id;
        }

    }
    public boolean updateRating(SQLiteDatabase db,int rating) {
        try {
            values.put("rating", rating);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Exploration", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateState(SQLiteDatabase db, int state, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("state", state);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Exploration", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRating(SQLiteDatabase db, int rating, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("rating", rating);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Exploration", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean finishExploration(SQLiteDatabase db, int state, int id, int itr_id) {
        ContentValues values = new ContentValues();
        try {
            values.put("state", state);
            values.put("itr_id", itr_id);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Exploration", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Integer> createExplorationList(SQLiteDatabase db, int p_id, int prj_id, int clt_id, ArrayList<Property> propertyList){
        Log.i(TAG, "createExplorationList: "+propertyList.size());
        ArrayList<Integer> explorationList = new ArrayList<>();
        int id;

        Exploration free = new Exploration(prj_id,p_id,clt_id,0,"Free");//insert exploration for free
        free.insertExploration(db);

        for(int i = 0; i < propertyList.size(); i++){
            Exploration exploration = new Exploration(prj_id,p_id,clt_id,propertyList.get(i).getId(),propertyList.get(i).getProperty());
            id = exploration.insertExploration(db);
            explorationList.add(id);
        }
        return explorationList;
    }

    public boolean delete() {

        return true;
    }

//    public class Type {
//        public static final int RELAX = 0 ;
//        public static final int FIST = 1;
//        public static final int FREE = 2;
//        public static final int SMOOTH = 3;
//        public static final int THICKNESS = 4;
//        public static final int WARMTH = 5;
//        public static final int FLEXIBILITY = 6;
//        public static final int SOFTNESS = 7;
//        public static final int ENJOYMENT = 8;
//
//    }
    public class State {
        public static final int INCOMPLETE = 0;
        public static final int FINISHED = 1;
        public static final int DELETED = 2;

    }
}
