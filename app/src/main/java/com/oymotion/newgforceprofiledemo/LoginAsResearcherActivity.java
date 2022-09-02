package com.oymotion.newgforceprofiledemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 04/12/2021
 * Time: 14:56
 * Description:
 */
public class LoginAsResearcherActivity extends AppCompatActivity {
    private static final String TAG = "LoginAsResearcherActivity";
    GForceDatabaseOpenHelper dbOpenHelper;
    SQLiteDatabase db;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String password = "UCLIC";

    @BindView(R.id.et_password)
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_researcher);
        ButterKnife.bind(this);
        this.setTitle("Login as Researcher");
        dbOpenHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbOpenHelper.getReadableDatabase();
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        String passwordEnter = et_password.getText().toString();
        boolean isCorrect = false;

        if (passwordEnter.isEmpty()) {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (!passwordEnter.equals(password)) {
                Toast.makeText(this, "Wrong Password.", Toast.LENGTH_LONG).show();
                return;
            } else {
                Toast.makeText(this, "Login success.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginAsResearcherActivity.this, ProjectListActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //NEED TO BACK reLogin
                startActivity(intent);
                finish();
            }

        }
    }

}

