package com.oymotion.newgforceprofiledemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
 * Date: 03/12/2021
 * Time: 10:43
 * Description:
 */
public class MaterialProfileActivity extends AppCompatActivity {
    private static final String TAG = "MaterialProfileActivity";


    private RecyclerView rv_explorationList;
    private ExplorationListAdapter explorationListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @BindView(R.id.tv_material_no)
    TextView tv_material_no;


    int prj_id;
    int clt_id;
//    int p_id;
    int position;
    int id_clicked;
    int ppt_id;
    String ppt_name;
    Intent intent;
    ArrayList<Exploration> explorationList = new ArrayList<>();


    private ExplorationListAdapter.OnItemListener onItemListener = new ExplorationListAdapter.OnItemListener() {
        @Override
        public void OnItemClickListener(View view, int position) {

            int childAdapterPosition = rv_explorationList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
            intent = new Intent(MaterialProfileActivity.this, InteractionActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            id_clicked = explorationList.get(childAdapterPosition).getId();
            ppt_id = explorationList.get(childAdapterPosition).getPpt_id();
            ppt_name = explorationList.get(childAdapterPosition).getPpt_name();
            intent.putExtra("ppt_id", ppt_id);
            intent.putExtra("ppt_name", ppt_name);
            intent.putExtra("clt_id", clt_id);
            intent.putExtra("explore_id", id_clicked);
            startActivity(intent);

        }

        @Override
        public void OnItemLongClickListener(View view, int position) {
            int childAdapterPosition = rv_explorationList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemLongClickListener: "+childAdapterPosition+"property_size:"+explorationList.size());
//
//            id_clicked = materialList.get(childAdapterPosition).getId();
//            Log.i(TAG, "OnItemLongClickListener: " +"pid: "+ id_clicked);
//            FragmentManager fm = getSupportFragmentManager();
//            popupDialog = new PopupDialog();
//            popupDialog.show(fm,"fragment_edit_delete_material");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_profile);
        ButterKnife.bind(this);
        setTitle("Material Profile");

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        updatePreference();//prj_id
        intent = this.getIntent();
        clt_id = intent.getIntExtra("clt_id",-1);
        position = intent.getIntExtra("position",-1) +1;
        tv_material_no.setText("Material" +position);
        initData();
        initView();

    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        explorationList = getData();
        explorationListAdapter = new ExplorationListAdapter(explorationList);
        explorationListAdapter.setOnItemListener(onItemListener);
    }

    private void initView() {
        rv_explorationList = (RecyclerView) findViewById(R.id.rv_exploration_list);
        // 设置布局管理器
        rv_explorationList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_explorationList.setAdapter(explorationListAdapter);
    }

    private void updatePreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prj_id = preferences.getInt("prj_id",-1);

    }

    @NotNull
    private ArrayList<Exploration> getData() {
        ArrayList<Exploration> data = new ArrayList<>();
        data = Exploration.getExplorationList(db, clt_id);
        return data;
    }

    @OnClick(R.id.btn_upload_photo)
    public void onUploadPhotoClick() {
        Intent intent = new Intent(MaterialProfileActivity.this,ImagePickerActivity.class);
//        what is set flag
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("clt_id",clt_id);
        intent.putExtra("position",position);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_answer_question)
    public void onAnswerQuestionClick() {
        Intent intent = new Intent(MaterialProfileActivity.this,AnswerDesignQuestionActivity.class);
//        what is set flag
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("p_id",p_id);
        intent.putExtra("clt_id",clt_id);
        intent.putExtra("position",position);
        startActivity(intent);
        finish();
    }



//    private void configureResultList() {
//        rv_explorationList.setHasFixedSize(true);
//        rv_explorationList.setItemAnimator(null);
//        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
//        rv_explorationList.setLayoutManager(recyclerLayoutManager);
//        explorationListAdapter = new ParticipantListAdapter();
//        rv_explorationList.setAdapter(explorationListAdapter);
//        rv_explorationList.setOnAdapterItemClickListener(view -> {
//            final int childAdapterPosition = rv_explorationList.getChildAdapterPosition(view);
//            final ScanResult itemAtPosition = explorationListAdapter.getItemAtPosition(childAdapterPosition);
//            onAdapterItemClick(itemAtPosition);
//
//            //when selected, item view is highlighted
//            view.setBackgroundColor(itemBackgroundColor);
//        });
//    }
//
//    private void onAdapterItemClick(ScanResult scanResults) {
////        final Intent intent = new Intent(this, DeviceActivity.class);
////        intent.putExtra(DeviceActivity.EXTRA_DEVICE_NAME, scanResults.getBluetoothDevice().getName());
////        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, scanResults.getBluetoothDevice().getAddress());
////        startActivity(intent);
//
//        //test for save bluetooth devices info and transfer
//        String extra_device_name = scanResults.getBluetoothDevice().getName();
//        String extra_mac_address = scanResults.getBluetoothDevice().getAddress();
//        int hand = Device.checkDeviceLR(db, extra_mac_address);
//        if(!isExist(extra_mac_address)) {
//            if (hand == 0){
//
//                leftSelected.setBackground(getDrawable(R.drawable.round_purple));
//            }else if(hand == 1){
//                rightSelected.setBackground(getDrawable(R.drawable.round_purple));
//            }
//            btList.add(new Bluetooth(extra_device_name,extra_mac_address));
//            count_selected ++;
//        }
//        if(count_selected == 2){
//            nextButton.setEnabled(true);
//        }
//
//    }


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
    protected void onResume() {
        super.onResume();
        explorationList = getData();
        explorationListAdapter.updateData(explorationList);


    }
}
