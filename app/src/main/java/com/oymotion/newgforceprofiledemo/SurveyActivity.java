package com.oymotion.newgforceprofiledemo;

import static java.util.Objects.isNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SurveyActivity extends AppCompatActivity {
    private static final String TAG = "SurveyActivity";
    

    @BindView(R.id.btn_next_survey)
    Button btn_next;

    @BindView(R.id.rg_likert)
    RadioGroup rg_liker;
//    RadioButton nan,nv ;
    RadioButton tempButton;


    TextView tv_question;
    TextView tv_low;
    TextView tv_high;


    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    int rating;
    int scale;

    int itr_id;//explore_id
    int explore_id;//explore_id
    int itr_type;//explore_id
    int ppt_id;//explore_id
    int p_id;
    int clt_id;
    int prj_id;
    Property property;
    String datafolder_timestamp;

    private FileWritingThread mFileWriteThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(SurveyActivity.this);
        this.setTitle("");
        tv_question = findViewById(R.id.tv_question);
        rg_liker = findViewById(R.id.rg_likert);
        tv_low = findViewById(R.id.tv_low);
        tv_high = findViewById(R.id.tv_high);


        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();


        rating = -1;
        prj_id = Project.getIDFromPreference(this);
        scale = Project.getSurveyScale(db,prj_id);

        Intent intent = this.getIntent();

        clt_id = intent.getIntExtra("clt_id",-1);
        p_id = intent.getIntExtra("p_id",-1);
        ppt_id = intent.getIntExtra("ppt_id",-1);
        explore_id = intent.getIntExtra("explore_id",-1);
        itr_type = intent.getIntExtra("itr_type",-1);
        itr_id = intent.getIntExtra("itr_id",-1);
        datafolder_timestamp = intent.getStringExtra("datafolder_timestamp");
        Log.i(TAG, "onCreate: ppt_id"+ppt_id);
        property = Property.getProperty(db, ppt_id);
        Log.i(TAG, "onCreate: property"+property.toString());
        Log.i(TAG, "onCreate: ppt_id"+ppt_id);
        initQuestion();
        initRadioGroup();
        rg_liker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = findViewById(checkedId);// 通过RadioGroup的findViewById方法，找到ID为checkedID的RadioButton
                String ratingStr = tempButton.getText().toString();;
                rating = Integer.valueOf(ratingStr);
                Log.i(TAG, "Selected Radio Button: " + tempButton.getText().toString());
                btn_next.setEnabled(true);
            }
        });

        Log.i(TAG, "Initial Information: " + "clt_id:" + clt_id +  "itr_type:" + itr_type);
        btn_next.setEnabled(false);

        mFileWriteThread = new FileWritingThread(datafolder_timestamp);


    }

    private void initQuestion() {
        Log.i(TAG, "initQuestion: ");

        String ppt_name = property.getProperty();
        String polar_low = property.getPolarLow();
        String polar_high = property.getPolarHigh();
        String html = "How much do you feel <b>" + ppt_name + "</b> ?.";
        tv_question.setText(Html.fromHtml(html, Typeface.BOLD));
        tv_low.setText(polar_low);
        tv_high.setText(polar_high);
    }

    private void initRadioGroup(){
        for(int i = 1; i <= scale; i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(String.valueOf(i));
            rg_liker.addView(radioButton);
        }
    }


    @OnClick(R.id.btn_next_survey)
    public void onNextClick() {
//        int radioButtonID = rg_liker.getCheckedRadioButtonId();
//        tempButton = findViewById(radioButtonID);
//        Toast.makeText(this, "Selected Radio Button: " + tempButton.getText(), Toast.LENGTH_LONG).show();
//        Clothes.updateQuality(db,clt_id,itr_type,likert);
//        Intent intentItr;
//        intentItr = new Intent(SurveyActivity.this,InteractionActivity.class);
//
//        clt_count = app.getClothesCount();
//        app.setInteractionState(Interaction.State.START);


//        switch (itr_type) {
//            case Interaction.Type.SMOOTH:
//                app.setInteractionType(Interaction.Type.THICKNESS);
//                startActivity(intentItr);
//                break;
//            case Interaction.Type.THICKNESS:
//                app.setInteractionType(Interaction.Type.ENJOYMENT);
//                startActivity(intentItr);
//                break;
//            case Interaction.Type.WARMTH:
//                app.setInteractionType(Interaction.Type.FLEXIBILITY);
//                startActivity(intentItr);
//                break;
//            case Interaction.Type.FLEXIBILITY:
//                app.setInteractionType(Interaction.Type.SOFTNESS);
//                startActivity(intentItr);
//                break;
//            case Interaction.Type.SOFTNESS:
//                app.setInteractionType(Interaction.Type.ENJOYMENT);
//                startActivity(intentItr);
//                break;
//

//            case Interaction.Type.ENJOYMENT://move this to another activity;
//                app.setClothesState(Clothes.State.FINISHED);
//                if(clt_count == 6){
//                    startActivity(intentEnd);
//                }else {
//                    app.updateClothesCount(clt_count);
//                    startActivity(intentImg);
//                }
//                break;

                //default
        Exploration.updateRating(db, rating, explore_id);
        List<String> experience_data = new ArrayList<String>();
        experience_data.add(String.valueOf(p_id));
        experience_data.add(String.valueOf(prj_id));
        experience_data.add(String.valueOf(itr_id));
        experience_data.add(String.valueOf(itr_type));
        experience_data.add(String.valueOf(clt_id));
        experience_data.add(String.valueOf(ppt_id));
        experience_data.add(String.valueOf(rating));

        mFileWriteThread.writeExperienceToFile(experience_data);
        finish();
        }

//    public void onRadioButtonClicked(View v) {
//        int radioButtonID = rg_liker.getCheckedRadioButtonId();
//        tempButton = findViewById(radioButtonID);
//        String likertStr = (String) tempButton.getText();;
//        rating = Integer.valueOf(likertStr);
//        Log.i(TAG, "Selected Radio Button: " + tempButton.getText());
//        btn_next.setEnabled(true);
//    }
}