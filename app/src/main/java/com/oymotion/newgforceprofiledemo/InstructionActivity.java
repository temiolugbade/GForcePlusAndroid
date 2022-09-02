package com.oymotion.newgforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionActivity extends AppCompatActivity {

    @BindView(R.id.bth_next)
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(InstructionActivity.this);
        this.setTitle("Instruction");
    }
    @OnClick(R.id.bth_next)
    public void OnNextClick(){
        Intent intent = new Intent(InstructionActivity.this, ScanDevicesActivity.class);
        intent.putExtra("test","test");
        startActivity(intent);
    }
}