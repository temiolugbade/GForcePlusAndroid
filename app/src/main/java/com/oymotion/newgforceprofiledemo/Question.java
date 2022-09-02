package com.oymotion.newgforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 03/12/2021
 * Time: 04:24
 * Description:
 */
public class Question {
    private static final String TAG = "Question";
    ContentValues values = new ContentValues();
    int id = -1;
    int prj_id;
    String content;
    int isMandatory;

    public Question(int id, int prj_id, String content, int isMandatory) {
        this.id = id;
        this.prj_id = prj_id;
        this.content = content;
        this.isMandatory = isMandatory;
    }

    public Question(int prj_id,String content, int isMandatory) {
        this.prj_id = prj_id;
        this.content = content;
        this.isMandatory = isMandatory;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public int getPrj_id() {
        return prj_id;
    }

    public int getIsMandatory() {
        return isMandatory;
    }

    public int insertQuestion(SQLiteDatabase db){
        values.put("prj_id", prj_id);
        values.put("content", content);
        values.put("isMandatory", isMandatory);
        values.put("state", 0);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Question", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public static boolean updateValue(SQLiteDatabase db, int id, String content){
        ContentValues values = new ContentValues();
        try {
            values.put("content", content);
            db.update("Question", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    static Question getQuestion(SQLiteDatabase db, int id) {
        Question question = new Question(-1,-1,null,-1);
        Cursor result = null;
        try {
            result = db.query("Question",null,"id = ?",new String[]{String.valueOf(id)},null,null,null);

        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        if(result.getCount() == 0){
            return null;
        }else{
            result.moveToNext();
            int mid = result.getInt(0);
            int prjId = result.getInt(1);
            String content = result.getString(2);
            int isMandatory = result.getInt(3);
            int state = result.getInt(4);
            question = new Question(mid,prjId,content,isMandatory);
            result.close();
            return question;
        }

    }
    static ArrayList<Question> getQuestionList(SQLiteDatabase db, int prj_id) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            Cursor result = db.query("Question",null,"prj_id = ? AND state != ?",new String[]{String.valueOf(prj_id),String.valueOf(State.DELETED)},null,null,null);
            while (result.moveToNext()){
                int mid = result.getInt(0);
                int prjId = result.getInt(1);
                String content = result.getString(2);
                int isMandatory = result.getInt(3);
                int state = result.getInt(4);
                Log.i(TAG, "getQuestionList: "+result.getColumnName(1));
                String question_con = result.getString(2);
                Question question = new Question(mid,prjId,content,isMandatory);
                questionList.add(question);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return questionList;
    }

    public static boolean deleteQuestion(SQLiteDatabase db, int id) {
        try {
            db.delete("Question","id=?", new String[]{String.valueOf(id)});//
            db.delete("Answer","qst_id=?", new String[]{String.valueOf(id)});//
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }
    public boolean updateQuestion(SQLiteDatabase db, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("content", content);
            values.put("isMandatory", isMandatory);
            db.update("Question", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }

    public class State{
        public static final int START = 0;
        public static final int DELETED = 2;
    }

}
