package com.oymotion.newgforceprofiledemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: Lili Lin
 * Date: 01/11/2021
 * Time: 21:58
 * Description:
 */
public class ExperimentSettingMenuActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentSettingMenuAc";

    @BindView(R.id.btn_participant)
    Button btn_participant;
    @BindView(R.id.btn_experiment)
    Button btn_experiment;

    int prj_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_setting_menu);
        ButterKnife.bind(this);
        this.setTitle("Setting Menu");

//        updatePreference();
        Intent intent = getIntent();
        if (intent != null) {
            prj_id = intent.getIntExtra("prj_id",-1);
            Log.i(TAG, "onCreate: "+prj_id);
        }

    }

    @OnClick(R.id.btn_participant)
    public void onParticipantClick() {
        Intent intent = new Intent(ExperimentSettingMenuActivity.this,ParticipantManageActivity.class);
        intent.putExtra("prj_id", prj_id);
        startActivity(intent);
    }

    @OnClick(R.id.btn_experiment)
    public void onExperimentClick() {
        Intent intent = new Intent(ExperimentSettingMenuActivity.this,ExperimentSettingActivity.class);
//        what is set flag
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//set this when back to home
//        intent.putExtra("p_id",p_id);
        intent.putExtra("prj_id", prj_id);
        Log.i(TAG, "onExperimentClick: prj_id->" +prj_id);
        startActivity(intent);
    }

    @OnClick(R.id.btn_back_to_home)
    public void onBackHomeClick() {
        Intent intent = new Intent(ExperimentSettingMenuActivity.this,LoginActivity.class);
//        intent.putExtra("prj_id", prj_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i(TAG, "onExperimentClick: prj_id->" +prj_id);
        startActivity(intent);
    }

    private void setIntent() {
        Intent intent = new Intent(ExperimentSettingMenuActivity.this,ExperimentSettingActivity.class);
//        what is set flag
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//set this when back to home
//        intent.putExtra("p_id",p_id);
        intent.putExtra("prj_id", prj_id);
        startActivity(intent);
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
