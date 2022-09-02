package com.oymotion.newgforceprofiledemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 02/12/2021
 * Time: 20:22
 * Description:
 */
public class EditOpenQuestionsActivity extends AppCompatActivity implements AddOpenQuestionDialog.NoticeDialogListener,PopupDialog.PopupDialogListener{

    private static final String TAG = "EditOpenQuestionsActivity";
    @BindView(R.id.btn_add_question)
    Button btn_add_question;

    private RecyclerView rv_openQuestionList;
    private OpenQuestionListAdapter openQuestionListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private AddOpenQuestionDialog addOpenQuestionDialog;
    private PopupDialog popupDialog;


    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    int prj_id = -1;
    int id;
    int id_clicked;
    ArrayList<Question> questionList;
    private OpenQuestionListAdapter.OnItemListener onItemListener = new OpenQuestionListAdapter.OnItemListener() {
        @Override
        public void OnItemClickListener(View view, int position) {
            int childAdapterPosition = rv_openQuestionList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
//            Intent intent = new Intent(OpenQuestionManageActivity.this, ExperimentSettingMenuActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//            p_id_clicked = questionList.get(childAdapterPosition).getPid();
//            intent.putExtra("p_id", p_id_clicked);
//            startActivity(intent);
        }

        @Override
        public void OnItemLongClickListener(View view, int position) {
            int childAdapterPosition = rv_openQuestionList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemLongClickListener: "+childAdapterPosition+"question_size:"+questionList.size());
            id_clicked = questionList.get(childAdapterPosition).getId();
            Log.i(TAG, "OnItemLongClickListener: " +"pid: "+ id_clicked);
            FragmentManager fm = getSupportFragmentManager();
            popupDialog = new PopupDialog();
            popupDialog.show(fm,"fragment_edit_delete_question");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_open_questions);
        ButterKnife.bind(this);
        setTitle("Manage Open Question");
        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        Intent intent = this.getIntent();
        prj_id = intent.getIntExtra("prj_id", -1);
        id = intent.getIntExtra("id", -1);

        Log.i(TAG, "onCreate: "+"prj_id:"+prj_id+"id"+id);
        btn_add_question = findViewById(R.id.btn_add_question);

        initData();
        initView();

    }


    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        questionList = getData();
        openQuestionListAdapter = new OpenQuestionListAdapter(questionList);
        openQuestionListAdapter.setOnItemListener(onItemListener);
    }

    private void initView() {
        rv_openQuestionList = (RecyclerView) findViewById(R.id.rv_open_question_list);
        // 设置布局管理器
        rv_openQuestionList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_openQuestionList.setAdapter(openQuestionListAdapter);
    }

    @NotNull
    private ArrayList<Question> getData() {
        ArrayList<Question> data = new ArrayList<>();
        data = Question.getQuestionList(db,prj_id);
        return data;
    }

    @OnClick(R.id.btn_add_question)
    public void onAddOpenQuestionClick() {
        Log.i(TAG, "onAddClick: tetstt");
        FragmentManager fm = getSupportFragmentManager();
        addOpenQuestionDialog = new AddOpenQuestionDialog(prj_id,-1);
        addOpenQuestionDialog.show(fm,"fragment_add_open_question");

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
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG, "onDialogPositiveClick: ");
        questionList = getData();
        openQuestionListAdapter.updateData(questionList);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }

    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        FragmentManager fm = getSupportFragmentManager();
        //reuse add dialog to update
        Log.i(TAG, "onDialogEditClick: "+prj_id+"-"+ id_clicked);
        addOpenQuestionDialog = new AddOpenQuestionDialog(prj_id, id_clicked);
        addOpenQuestionDialog.show(fm,"fragment_add_question");
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        new AlertDialog.Builder(this).setTitle("Delete OpenQuestion " + id_clicked +"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Question.deleteQuestion(db, id_clicked);
                        Log.i(TAG, "delete question: "+ id_clicked);
                        questionList = getData();
                        openQuestionListAdapter.updateData(questionList);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
