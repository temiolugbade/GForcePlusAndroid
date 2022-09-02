package com.oymotion.newgforceprofiledemo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 03/12/2021
 * Time: 02:08
 * Description:
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
//    @Nullable
//    @BindView(R.id.btn_test)
//    Button btn;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        this.setTitle("TEST");

    }

    @OnClick(R.id.btn_test111)
    public void onTestClick(){
        Log.i(TAG, "onTestClick: ");

    }
}
