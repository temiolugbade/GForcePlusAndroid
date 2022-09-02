package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class Interaction {
    private static final String TAG = "Interaction";
    int id;
    int p_id;
    int prj_id;
    int rating;
    int clt_id;
    int expl_id;//exploration_id,
    int type;//property id, ppt_id
    int state;

    ContentValues values = new ContentValues();


    public Interaction(int id, int prj_id, int p_id, int clt_id, int expl_id, int type, int state) {
        this.id = id;
        this.prj_id = prj_id;
        this.p_id = p_id;
        this.clt_id = clt_id;
        this.expl_id = expl_id;
        this.type = type;
        this.state = state;
    }

    public Interaction(int prj_id, int p_id, int clt_id, int expl_id, int ppt_id) {
        this.prj_id = prj_id;
        this.p_id = p_id;
        this.clt_id = clt_id;
        this.expl_id = expl_id;
        this.type = ppt_id;
    }


    public int getCltid() {
        return clt_id;
    }

    public int getPid() {
        return p_id;
    }

    public int getType() {
        return type;
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
    
    static ArrayList<Interaction> getInteractionList(SQLiteDatabase db) {
        ArrayList<Interaction> interactionList = new ArrayList<>();
        try {
            Cursor result = db.query("Interaction",null,"state != ?",new String[]{String.valueOf(Project.State.DELETED)},null,null,null);
            Log.i(TAG, "getInteractionList: "+result);
            while (result.moveToNext()){
                Log.i(TAG, "getInteractionList: "+result.getColumnName(1));
                int id = result.getInt(result.getColumnIndex("id"));
                int p_id = result.getInt(result.getColumnIndex("p_id"));
                int prj_id = result.getInt(result.getColumnIndex("prj_id"));
                int clt_id = result.getInt(result.getColumnIndex("clt_id"));
                int expl_id = result.getInt(result.getColumnIndex("expl_id"));
                int type = result.getInt(result.getColumnIndex("type"));//itr_type = ppt_id
                int state = result.getInt(result.getColumnIndex("state"));
                Interaction interaction = new Interaction(id,prj_id,p_id,clt_id,expl_id,type,state);
                interactionList.add(interaction);

            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return interactionList;
    }


    public int insertInteraction(SQLiteDatabase db) {
        values.put("p_id", p_id);
        values.put("prj_id",prj_id);
        values.put("clt_id", clt_id);
        values.put("expl_id", expl_id);
        values.put("type", type);
        values.put("state", State.START);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Interaction", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            Log.e(TAG, e.getMessage());
            return id;
        }

    }


    public boolean updateState(SQLiteDatabase db, int state) {
        try {
            values.put("state", state);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Interaction", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() {

        return true;
    }

    public class Type {
        public static final int ERROR = -1 ;
        public static final int RELAX = -2 ;
        public static final int FIST = -3;
        public static final int FREE = 2;
        public static final int SMOOTH = 3;
        public static final int THICKNESS = 4;
        public static final int WARMTH = 5;
        public static final int FLEXIBILITY = 6;
        public static final int SOFTNESS = 7;
        public static final int ENJOYMENT = 8;

    }
    public class State {
        public static final int START = 0;
        public static final int FINISHED = 1;
        public static final int DELETED = 2;
        public static final int FAILED = 3;
        public static final int CONNECT_ERROR = 4;

    }
}
