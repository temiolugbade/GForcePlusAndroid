package com.oymotion.newgforceprofiledemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 25/07/2021
 * Time: 05:20
 * Description:
 */
public class SurveyEnjoymentActivity extends AppCompatActivity {
    private static final String TAG = "SurveyEnjoymentActivity";


    @BindView(R.id.btn_next)
    Button btn_next;

//    @BindView(R.id.et_enjoy_touch)
//    EditText et_enjoy_touch;

    RadioGroup rg_liker;
    //    RadioButton nan,nv ;
    RadioButton radioButton;


    TextView tv_question;

//    Runnable runnable;
//    Handler handler;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    MyApplication app;

    int itr_type;
    int clt_id;
    int likert;
    int clt_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_enjoy);
        ButterKnife.bind(SurveyEnjoymentActivity.this);
        this.setTitle("Feedback");
        tv_question = findViewById(R.id.tv_question);
        rg_liker = findViewById(R.id.rg_likert);


        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();

        app = (MyApplication) getApplication();
        itr_type = app.getInteractionType();
        clt_id = app.getClothesID();
        likert = -1;


        Log.i(TAG, "Initial Information: " + "clt_id:" + clt_id +  "itr_type:" + itr_type);
        btn_next.setEnabled(false);

//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                String enjoy_touch_str = et_enjoy_touch.getText().toString();
//                int radioButtonID = rg_liker.getCheckedRadioButtonId();
//                Log.i(TAG,et_enjoy_touch+String.valueOf(radioButtonID));
//                if (!enjoy_touch_str.isEmpty() && radioButtonID!= -1){
//                   runOnUiThread(new Runnable() {
//                       @Override
//                       public void run() {
//                           btn_next.setEnabled(true);
//                       }
//                   });
//
//                }
//                handler.postDelayed(this, 1000);
//            }
//        };
//        handler.post(runnable);

    }
//    @OnClick(R.id.btn_next)
//    public void onNextClick() {
////        String enjoy_touch_str = et_enjoy_touch.getText().toString();
////        if(enjoy_touch_str.isEmpty()){
////            Toast.makeText(this, "The answer can not be null." + radioButton.getText(), Toast.LENGTH_LONG).show();
////            return;
////        }
//
//        int radioButtonID = rg_liker.getCheckedRadioButtonId();
//        if(radioButtonID == -1){
//            Toast.makeText(this, "Please select the answer for second question" + radioButton.getText(), Toast.LENGTH_LONG).show();
//            return;
//        }
////        radioButton = findViewById(radioButtonID);
////        Toast.makeText(this, "Selected Radio Button: " + radioButton.getText(), Toast.LENGTH_LONG).show();
//        Clothes.updateQuality(db,clt_id,itr_type,likert);
////        Clothes.updateEnjoyText(db,clt_id,enjoy_touch_str);
//        Intent intentImg;
//        Intent intentEnd;
//        intentImg = new Intent(SurveyEnjoymentActivity.this,ImagePickerActivity.class);
//        intentEnd = new Intent(SurveyEnjoymentActivity.this,EndActivity.class);
//
//        clt_count = app.getClothesCount();
//        app.setClothesState(Clothes.State.FINISHED);
//        if(clt_count == 6){
//            startActivity(intentEnd);
//        }else {
//            app.updateClothesCount(clt_count);
//            startActivity(intentImg);
//        }
//
//        }
//
//    public void onRadioButtonClicked(View v) {
//        int radioButtonID = rg_liker.getCheckedRadioButtonId();
//        radioButton = findViewById(radioButtonID);
//        String likertStr = (String) radioButton.getText();;
//        likert = Integer.valueOf(likertStr);
//        Log.i(TAG, "Selected Radio Button: " + radioButton.getText());
//        btn_next.setEnabled(true);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(runnable);
    }
}
