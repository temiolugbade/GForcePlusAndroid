package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class Clothes {
    private static final String TAG = "Clothes";
    int p_id;
//    int e_id;
    byte[] img_cloth;
    byte[] img_label;
    int prj_id;
    ContentValues values = new ContentValues();

    int state = 0;
    int id = -1;
    public Clothes(int id, int prj_id, int p_id,byte[] img_cloth,byte[] img_label,int state) {
        this.id = id;
        this.prj_id = prj_id;
        this.p_id = p_id;
        this.img_cloth = img_cloth;
        this.img_label = img_label;
        this.state = state;
    }

    public Clothes(int prj_id, int p_id) {
        this.prj_id = prj_id;
        this.p_id = p_id;
    }

    public int getState() { return state; }

    public static String getTAG() {
        return TAG;
    }

    public int getP_id() {
        return p_id;
    }

    public byte[] getImg_cloth() {
        return img_cloth;
    }

    public byte[] getImg_label() {
        return img_label;
    }

    public int getPrj_id() {
        return prj_id;
    }

    public ContentValues getValues() {
        return values;
    }

    public int getId() {
        return id;
    }

    public int insertClothes(SQLiteDatabase db){
        values.put("p_id", p_id);
        values.put("prj_id",prj_id);
//        values.put("img_cloth", image);
        values.put("state", state);
//        values.put("img_label", z);
//        values.put("c_soft", user_id);
//        values.put("c_warmth", user_id);
//        values.put("c_thickness", user_id);
//        values.put("c_smooth", user_id);
//        values.put("c_enjoyment", section);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Clothes", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public boolean addClothesImg(SQLiteDatabase db, byte[] image){
        try {
            Log.i(TAG, String.valueOf(id));
            values.put("img_cloth", image);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            values.clear();
            return false;
        }
    }
    public boolean addLabelImg(SQLiteDatabase db, byte[] image){
        try {
            Log.i(TAG, String.valueOf(id));
            Log.i(TAG, "before update");
            values.put("img_label", image);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            values.clear();
            return false;
        }
    }

    public static boolean updateState(SQLiteDatabase db, int id, int state){
        ContentValues values = new ContentValues();
        try {
            values.put("state", state);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }

    static Clothes getClothes(SQLiteDatabase db, int id) {
        Clothes clothes;
        Cursor result = null;
        try {
            result = db.query("Clothes",null," id = ?",new String[]{String.valueOf(id)},null,null,null);

        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        if(result.getCount() == 0){
            return null;
        }else{
            result.moveToNext();
            int mId = result.getInt(result.getColumnIndex("id"));
            int prj_id = result.getInt(result.getColumnIndex("prj_id"));
            int p_id = result.getInt(result.getColumnIndex("p_id"));
            byte[] img_cloth = result.getBlob(result.getColumnIndex("img_cloth"));
            byte[] img_label = result.getBlob(result.getColumnIndex("img_label"));
            int state = result.getInt(result.getColumnIndex("state"));
            clothes = new Clothes(mId,prj_id,p_id,img_cloth,img_label,state);
            result.close();
            return clothes;

        }

    }


//    public static void updateQuality(SQLiteDatabase db, int id, int clothes,int likert){
//        ContentValues values = new ContentValues();
//        try {
//            switch (clothes){
//                case Interaction.Type.SMOOTH:
//                    values.put("c_smooth", likert);
//                    break;
//                case Interaction.Type.THICKNESS:
//                    values.put("c_thickness", likert);
//                    break;
//                case Interaction.Type.WARMTH:
//                    values.put("c_warmth", likert);
//                    break;
//                case Interaction.Type.FLEXIBILITY:
//                    values.put("c_flexibility", likert);
//                    break;
//                case Interaction.Type.SOFTNESS:
//                    values.put("c_soft", likert);
//                    break;
//                case Interaction.Type.ENJOYMENT:
//                    values.put("c_enjoyment", likert);
//                    break;
//            }
////            values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
//            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
//            values.clear();
//        } catch (NumberFormatException e) {
//            values.clear();
//            e.printStackTrace();
//        }
//    }

//    public static void updateEnjoyText(SQLiteDatabase db, int id, String str){
//        ContentValues values = new ContentValues();
//        try {
//            values.put("c_enjoy_touch", str);
//            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
//            values.clear();
//        } catch (NumberFormatException e) {
//            values.clear();
//            e.printStackTrace();
//        }
//    }

    public static ArrayList<Clothes> getMaterialList(SQLiteDatabase db, int prj_id, int p_id) {
        ArrayList<Clothes> materialList = new ArrayList<>();
        String prj_id_str = String.valueOf(prj_id);
        String p_id_str = String.valueOf(p_id);
        Cursor result = db.query("Clothes",null,"prj_id = ? AND p_id=? AND state != ?",new String[]{prj_id_str,p_id_str,String.valueOf(State.DELETED)},null,null,null);
        Log.i(TAG, "getMaterialList: "+result);
        while (result.moveToNext()) {
            Log.i(TAG, "getMaterialList: " + result.getColumnName(1));
            int mId = result.getInt(result.getColumnIndex("id"));
            int mPrj_id = result.getInt(result.getColumnIndex("prj_id"));
            int mP_id = result.getInt(result.getColumnIndex("p_id"));
            byte[] img_cloth = result.getBlob(result.getColumnIndex("img_cloth"));
            byte[] img_label = result.getBlob(result.getColumnIndex("img_label"));
            int state = result.getInt(result.getColumnIndex("state"));
            Clothes clothes = new Clothes(mId,mPrj_id,mP_id,img_cloth,img_label,state);
            materialList.add(clothes);
        }
        result.close();
        return materialList;
    }
    public static boolean deleteClothes(SQLiteDatabase db, int id) {

        ContentValues values = new ContentValues();
        try {
            values.put("state", Clothes.State.DELETED);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//
            db.update("Exploration", values, "clt_id=?", new String[]{String.valueOf(id)});//
            db.update("Interaction", values, "clt_id=?", new String[]{String.valueOf(id)});//
            db.update("Answer", values, "clt_id=?", new String[]{String.valueOf(id)});//
//            db.update("Interaction", values, "p_id=?", new String[]{String.valueOf(prj_id)});
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }



    public class State {
        public static final int START = 0;
        public static final int FINISHED = 1;
        public static final int DELETED = 2;

    }
}
