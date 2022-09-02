package com.oymotion.newgforceprofiledemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 08/12/2021
 * Time: 00:11
 * Description:
 */
public class AnswerDesignQuestionActivity extends AppCompatActivity {

    private static final String TAG = "AnswerDesignQuestionActivity";

//    private RecyclerView rv_questionList;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private RecyclerView rv_question_answer_list;
    private AnswerListAdapter answerListAdapter;
    private RecyclerView.LayoutManager layoutManager;


    int prj_id = -1;
    int clt_id;//？？？？
    int qst_id;
    ArrayList<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_design_question);
        ButterKnife.bind(this);
        setTitle("Questions");
        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        prj_id = Project.getIDFromPreference(this);
        Intent intent = this.getIntent();
        clt_id = intent.getIntExtra("clt_id",-1);
        Log.i(TAG, "onCreate: "+prj_id+"->"+clt_id);
//
        initData();
        initView();

    }


    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        answerListAdapter = new AnswerListAdapter(db,getData(),clt_id);
    }

    private void initView() {
        rv_question_answer_list = (RecyclerView) findViewById(R.id.rv_question_answer_list);
        // 设置布局管理器
        rv_question_answer_list.setLayoutManager(layoutManager);
        // 设置adapter
        rv_question_answer_list.setAdapter(answerListAdapter);
    }

    @NotNull
    private ArrayList<Question> getData() {
        ArrayList<Question> data = new ArrayList<>();
        data = Question.getQuestionList(db,prj_id);
        return data;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
