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
 * Date: 02/12/2021
 * Time: 20:21
 * Description:
 */
public class EditPropertiesActivity extends AppCompatActivity implements AddPropertyDialog.NoticeDialogListener,PopupDialog.PopupDialogListener{

    private static final String TAG = "EditPropertiesActivity";

    @BindView(R.id.btn_add_property)
    Button btn_add_property;

    private RecyclerView rv_propertyList;
    private PropertyListAdapter propertyListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddPropertyDialog addPropertyDialog;
    private PopupDialog popupDialog;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    int prj_id = -1;
    int id;
    int id_clicked;
    ArrayList<Property> propertyList;

    private PropertyListAdapter.OnItemListener onItemListener = new PropertyListAdapter.OnItemListener() {
        @Override
        public void OnItemClickListener(View view, int position) {
            int childAdapterPosition = rv_propertyList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
//            Intent intent = new Intent(PropertyManageActivity.this, ExperimentSettingMenuActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//            p_id_clicked = propertyList.get(childAdapterPosition).getPid();
//            intent.putExtra("p_id", p_id_clicked);
//            startActivity(intent);
        }

        @Override
        public void OnItemLongClickListener(View view, int position) {
            int childAdapterPosition = rv_propertyList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemLongClickListener: "+childAdapterPosition+"property_size:"+propertyList.size());

            id_clicked = propertyList.get(childAdapterPosition).getId();
            Log.i(TAG, "OnItemLongClickListener: " +"pid: "+ id_clicked);
            FragmentManager fm = getSupportFragmentManager();
            popupDialog = new PopupDialog();
            popupDialog.show(fm,"fragment_edit_delete_property");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_properties);
        ButterKnife.bind(this);
        setTitle("Manage Property");
        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        Intent intent = this.getIntent();
        prj_id = intent.getIntExtra("prj_id", -1);
//        id = intent.getIntExtra("id", -1);//？？？？

        Log.i(TAG, "onCreate: "+"prj_id:"+prj_id+"id"+id);
        btn_add_property = findViewById(R.id.btn_add_property);

        initData();
        initView();

    }


    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        propertyList = getData();
        propertyListAdapter = new PropertyListAdapter(propertyList);
        propertyListAdapter.setOnItemListener(onItemListener);
    }

    private void initView() {
        rv_propertyList = (RecyclerView) findViewById(R.id.rv_property);
        // 设置布局管理器
        rv_propertyList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_propertyList.setAdapter(propertyListAdapter);
    }

    @NotNull
    private ArrayList<Property> getData() {
        ArrayList<Property> data = new ArrayList<>();
        data = Property.getPropertyList(db, prj_id);
        return data;
    }

    @OnClick(R.id.btn_add_property)
    public void onAddPropertyClick() {
        Log.i(TAG, "onAddClick: "+"prj_id:"+prj_id+"id"+id);
        FragmentManager fm = getSupportFragmentManager();
        addPropertyDialog = new AddPropertyDialog(prj_id, -1);
        addPropertyDialog.show(fm,"fragment_add_property");
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
    public void onDialogPositiveClick(DialogFragment dialog, int id) {
        Log.i(TAG, "onDialogPositiveClick: ");
        propertyList = getData();
        propertyListAdapter.updateData(propertyList);
        this.id = id;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }

    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        FragmentManager fm = getSupportFragmentManager();
        //reuse add dialog to update
        Log.i(TAG, "onDialogEditClick: prj_id:"+prj_id+"clicked_id:"+ id_clicked);
        addPropertyDialog = new AddPropertyDialog(prj_id, id_clicked);
        addPropertyDialog.show(fm,"fragment_add_property");
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        new AlertDialog.Builder(this).setTitle("Delete Property " + id_clicked +"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Property.deleteProperty(db, id_clicked);
                        Log.i(TAG, "delete property: "+ id_clicked);
                        propertyList = getData();
                        propertyListAdapter.updateData(propertyList);
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
