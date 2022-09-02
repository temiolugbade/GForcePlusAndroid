package com.oymotion.newgforceprofiledemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EndActivity extends AppCompatActivity {
    private static final String TAG = "EndActivity";
    MyApplication app;

    @BindView(R.id.btn_exit)
    Button btn_exit;

    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;
    int e_id = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        ButterKnife.bind(EndActivity.this);
        this.setTitle("END");
        this.setTitle("Login");

        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getWritableDatabase();
        app = (MyApplication) getApplication();
        app.setExperimentState(Experiment.State.FINISHED);
        e_id = app.getExperimentID();
        Experiment.updateExpProgress(db,e_id,Experiment.State.FINISHED);
    }

    @OnClick(R.id.btn_exit)
    public void onNextClick() {
        Log.i(TAG, "exit");
//        try{
//            finish();
//            System.exit(0);
//        }catch (Exception e){
//            Log.e(TAG, "EXIT ERROR:" + e.getMessage());
//        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
        db.close();
        dbHelper.close();
        super.onDestroy();
    }
}