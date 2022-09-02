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
 * Time: 21:15
 * Description:
 */
public class Answer {

    private static final String TAG = "Answer";

    int id;
    int prj_id;
    int p_id;
    int clt_id;
    int ppt_id = -1;
    int qst_id;
    String content;
    int state = 0;


    ContentValues values = new ContentValues();


    public Answer(int p_id, int prj_id, int clt_id, int ppt_id, int qst_id, String content) {
        this.p_id = p_id;
        this.prj_id = prj_id;
        this.clt_id = clt_id;
        this.ppt_id = ppt_id;
        this.qst_id = qst_id;
        this.content = content;
    }

    public Answer(int p_id, int prj_id, int clt_id, int qst_id, String content) {
        this.p_id = p_id;
        this.prj_id = prj_id;
        this.clt_id = clt_id;
        this.qst_id = qst_id;
        this.content = content;
    }


    public Answer(int id,int p_id, int prj_id, int clt_id, int ppt_id, int qst_id, String content,int state) {
        this.id = id;
        this.p_id = p_id;
        this.prj_id = prj_id;
        this.clt_id = clt_id;
        this.ppt_id = ppt_id;
        this.qst_id = qst_id;
        this.content = content;
        this.state = state;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getId() {
        return id;
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

    public int getQst_id() {
        return qst_id;
    }

    public String getContent() {
        return content;
    }

    public int getState() {
        return state;
    }

    public ContentValues getValues() {
        return values;
    }

    static Answer getQuestionAnswer(SQLiteDatabase db, int qst_id, int clt_id) {
        Answer Answer;
        Cursor result = null;
        try {
            result = db.query("Answer",null,"qst_id = ? and clt_id = ?",new String[]{String.valueOf(qst_id),String.valueOf(clt_id)},null,null,null);

        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        if(result.getCount() == 0){
            return null;
        }else{
            result.moveToNext();
            int id = result.getInt(0);
            int prj_id = result.getInt(1);
            int p_id = result.getInt(2);
            int mclt_id = result.getInt(3);
            int ppt_id = result.getInt(4);
            int mqst_id = result.getInt(5);
            String content = result.getString(6);
            int state = result.getInt(7);

            Answer = new Answer(id,prj_id,p_id,clt_id,ppt_id,mqst_id,content,state);

            result.close();
            return Answer;
        }

    }

    static Answer getExplorationAnswer(SQLiteDatabase db, int ppt_id) {
        Answer Answer;
        Cursor result = null;
        try {
            result = db.query("Answer",null,"ppt_id = ?",new String[]{String.valueOf(ppt_id)},null,null,null);

        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        if(result.getCount() == 0){
            return null;
        }else{
            result.moveToNext();
            int id = result.getInt(0);
            int prj_id = result.getInt(1);
            int p_id = result.getInt(2);
            int clt_id = result.getInt(3);
            int mppt_id = result.getInt(4);
            int mqst_id = result.getInt(5);
            String content = result.getString(6);
            int state = result.getInt(7);

            Answer = new Answer(id,prj_id,p_id,clt_id,mppt_id,mqst_id,content,state);

            result.close();
            return Answer;
        }
    }

    static ArrayList<Answer> getAnswerList(SQLiteDatabase db, int prjId) {
        ArrayList<Answer> answerList = new ArrayList<>();
        try {
            Cursor result = db.query("Answer",null,"prj_id=? AND state != ?", new String[]{String.valueOf(prjId),String.valueOf(Clothes.State.DELETED)},null,null,null);
            Log.i(TAG, "getAnswerList: "+result);
            while (result.moveToNext()){
                Log.i(TAG, "getAnswerList: "+result.getColumnName(1));
                int id = result.getInt(0);
                int prj_id = result.getInt(1);
                int p_id = result.getInt(2);
                int clt_id = result.getInt(3);
                int ppt_id = result.getInt(4);
                int qst_id = result.getInt(5);
                String content = result.getString(6);
                int state = result.getInt(7);
                Answer answer = new Answer(id,prj_id,p_id,clt_id,ppt_id,qst_id,content,state);
                answerList.add(answer);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return answerList;
    }


    public int insertAnswer(SQLiteDatabase db) {
        values.put("prj_id",prj_id);
        values.put("p_id", p_id);
        values.put("clt_id", clt_id);
        values.put("ppt_id", ppt_id);
        values.put("qst_id",qst_id);
        values.put("content", content);
        values.put("state", State.FINISHED);
        values.put("timestamp", DatabaseUtil.getTimestamp());

        try {
            id = (int) db.insert("Answer", null, values);
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
            db.update("Answer", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateContent(SQLiteDatabase db, int clt_id, int qst_id, String content) {
        ContentValues values = new ContentValues();
        try {
            values.put("content", content);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Answer", values, "clt_id = ? AND qst_id =?", new String[]{String.valueOf(clt_id),String.valueOf(qst_id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteAnswer(SQLiteDatabase db, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("state", State.DELETED);
            db.update("Answer", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public class State {
        public static final int INCOMPLETE = 0;
        public static final int FINISHED = 1;
        public static final int DELETED = 2;

    }
}
