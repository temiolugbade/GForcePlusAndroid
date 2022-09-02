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
 * Date: 19/11/2021
 * Time: 15:20
 * Description:
 */
public class ParticipantManageActivity extends AppCompatActivity implements AddParticipantDialog.NoticeDialogListener,PopupDialog.PopupDialogListener {
    private static final String TAG = "ParticipantManageActivity";

    @BindView(R.id.btn_add_participant)
    Button btn_add;

    private RecyclerView rv_participantList;
    private ParticipantListAdapter participantListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddParticipantDialog addParticipantDialog;
    private PopupDialog popupDialog;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    int prj_id = -1;
    int p_id_clicked;
    ArrayList<Participant> participantList;

    private ParticipantListAdapter.OnItemListener onItemListener = new ParticipantListAdapter.OnItemListener() {
        @Override
        public void OnItemClickListener(View view, int position) {
            int childAdapterPosition = rv_participantList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
//            Intent intent = new Intent(ParticipantManageActivity.this, ExperimentSettingMenuActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//            p_id_clicked = participantList.get(childAdapterPosition).getPid();
//            intent.putExtra("p_id", p_id_clicked);
//            startActivity(intent);
        }

        @Override
        public void OnItemLongClickListener(View view, int position) {
            int childAdapterPosition = rv_participantList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemLongClickListener: "+childAdapterPosition);
            p_id_clicked = participantList.get(childAdapterPosition).getP_id();
            Log.i(TAG, "OnItemLongClickListener: " +"pid: "+p_id_clicked);
            FragmentManager fm = getSupportFragmentManager();
            popupDialog = new PopupDialog();
            popupDialog.show(fm,"fragment_edit_delete_participant");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_manage);
        ButterKnife.bind(this);
        setTitle("Manage Participants");

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        Intent intent = this.getIntent();
        prj_id = intent.getIntExtra("prj_id", -1);

        Log.i(TAG, "onCreate: "+prj_id);
        btn_add = findViewById(R.id.btn_add_participant);
        initData();
        initView();
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        participantList = getData();
        participantListAdapter = new ParticipantListAdapter(participantList);
        participantListAdapter.setOnItemListener(onItemListener);

    }

    private void initView() {
        rv_participantList = (RecyclerView) findViewById(R.id.rv_participant_list);
        // 设置布局管理器
        rv_participantList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_participantList.setAdapter(participantListAdapter);
    }

    @NotNull
    private ArrayList<Participant> getData() {
        ArrayList<Participant> data = new ArrayList<>();
        data = Participant.getParticipantList(db,prj_id);
        return data;
    }

    @OnClick(R.id.btn_add_participant)
    public void onAddClick() {
        FragmentManager fm = getSupportFragmentManager();
        addParticipantDialog = new AddParticipantDialog(prj_id,-1);
        addParticipantDialog.show(fm,"fragment_add_participant");

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
    public void onDialogPositiveClick(DialogFragment dialog, int p_id) {
        Log.i(TAG, "onDialogPositiveClick: ");
        participantList = getData();
        participantListAdapter.updateData(participantList);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }



    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        FragmentManager fm = getSupportFragmentManager();
        //reuse add dialog to update
        Log.i(TAG, "onDialogEditClick: "+prj_id+"-"+p_id_clicked);
        addParticipantDialog = new AddParticipantDialog(prj_id, p_id_clicked);
        addParticipantDialog.show(fm,"fragment_add_participant");
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        new AlertDialog.Builder(this).setTitle("Delete Participant " +p_id_clicked+"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Participant.deleteParticipant(db, prj_id, p_id_clicked);
                        Log.i(TAG, "delete participant: "+p_id_clicked);
                        participantList = getData();
                        participantListAdapter.updateData(participantList);
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
