package com.oymotion.gforceprofiledemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * Date: 05/12/2021
 * Time: 02:30
 * Description:
 */
public class ProjectListActivity extends AppCompatActivity implements AddProjectDialog.NoticeDialogListener, ChangeCurrentProjectDialog.NoticeDialogListener,PopupDialog.PopupDialogListener {
    private static final String TAG = "ProjectManageActivity";

    @BindView(R.id.btn_add_project)
    Button btn_add;


    @BindView(R.id.tv_prj_id)
    TextView tv_prj_id;
    @BindView(R.id.tv_prj_name)
    TextView tv_prj_name;
    @BindView(R.id.tv_researcher)
    TextView tv_researcher;
    @BindView(R.id.tv_explore_time)
    TextView tv_explore_time;
    @BindView(R.id.tv_scale)
    TextView tv_scale;

    private RecyclerView rv_projectList;
    private ProjectListAdapter projectListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddProjectDialog addProjectDialog;
    private PopupDialog popupDialog;
    private ChangeCurrentProjectDialog changeCurrentProjectDialog;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;

    int prj_id_current;
    int prj_id_clicked;
    ArrayList<Project> projectList;

    private ProjectListAdapter.OnItemListener onItemListener = new ProjectListAdapter.OnItemListener() {
        @Override
        public void OnItemClickListener(View view, int position) {
            int childAdapterPosition = rv_projectList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
            Intent intent = new Intent(ProjectListActivity.this, ExperimentSettingMenuActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            prj_id_clicked = projectList.get(childAdapterPosition).getPrj_id();
            intent.putExtra("prj_id", prj_id_clicked);
            startActivity(intent);
        }

        @Override
        public void OnItemLongClickListener(View view, int position) {
            int childAdapterPosition = rv_projectList.getChildAdapterPosition(view);
            prj_id_clicked = projectList.get(childAdapterPosition).getPrj_id();
            Log.i(TAG, "OnItemLongClickListener: " +childAdapterPosition);
            FragmentManager fm = getSupportFragmentManager();
            popupDialog = new PopupDialog();
            popupDialog.show(fm,"fragment_edit_delete_project");

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        ButterKnife.bind(this);
        setTitle("Manage Projects");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prj_id_current = preferences.getInt("prj_id", -1);

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        initData();
        initView();
        updateCurrentProject();


    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        projectList = getData();
        projectListAdapter = new ProjectListAdapter(projectList);
        projectListAdapter.setOnItemListener(onItemListener);
        btn_add = findViewById(R.id.btn_add_project);
    }

    private void initView() {
        rv_projectList = (RecyclerView) findViewById(R.id.rv_project_list);
        // 设置布局管理器
        rv_projectList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_projectList.setAdapter(projectListAdapter);
    }

    @NotNull
    private ArrayList<Project> getData() {
        ArrayList<Project> data = new ArrayList<>();
        data = Project.getProjectList(db);
        return data;
    }

    @OnClick(R.id.btn_add_project)
    public void onAddClick() {
        FragmentManager fm = getSupportFragmentManager();
        addProjectDialog = new AddProjectDialog(-1);//when parameter = -1, enter add project dialog
        addProjectDialog.show(fm,"fragment_add_project");

    }

    @OnClick(R.id.btn_change_current_project)
    public void onChangeProjectClick() {
        FragmentManager fm = getSupportFragmentManager();
        changeCurrentProjectDialog = new ChangeCurrentProjectDialog(projectList, prj_id_current);
        changeCurrentProjectDialog.show(fm,"fragment_add_project");

    }


    @OnClick(R.id.btn_back_to_home)
    public void onBackHomeClick() {
        Intent intent = new Intent(ProjectListActivity.this,LoginActivity.class);
//        intent.putExtra("prj_id", prj_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i(TAG, "onExperimentClick: prj_id->" + prj_id_current);
        startActivity(intent);
    }


    public void updateCurrentProject(){
        Project project = Project.getProject(db, prj_id_current);
        if(project != null) {
            tv_prj_id.setText(String.valueOf(project.getPrj_id()));
            tv_prj_name.setText(project.getPrj_name());
            tv_researcher.setText(project.getResearcher());
            tv_explore_time.setText(String.valueOf(project.getExplore_time()));
            tv_scale.setText(String.valueOf(project.getScale()));
        }

    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG, "onDialogPositiveClick: ");
        projectList = getData();
        projectListAdapter.updateData(projectList);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }


    @Override
    public void onChangeDialogPositiveClick(DialogFragment dialog, int prj_id) {
        this.prj_id_current = prj_id;
        editor = preferences.edit();
        editor.putInt("prj_id", prj_id);
        editor.apply();
        editor.clear();
        updateCurrentProject();
    }

    @Override
    public void onChangeDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

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
    public void onDialogEditClick(DialogFragment dialog) {
        FragmentManager fm = getSupportFragmentManager();
        //reuse add dialog to update
        addProjectDialog = new AddProjectDialog(prj_id_clicked);
        addProjectDialog.show(fm,"fragment_add_project");
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        new AlertDialog.Builder(this).setTitle("Delete Project " +prj_id_clicked+"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Project.deleteProject(db, prj_id_clicked);
                        Log.i(TAG, "delete project: "+prj_id_clicked);
                        projectList = getData();
                        projectListAdapter.updateData(projectList);
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
