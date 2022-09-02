package com.oymotion.newgforceprofiledemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Date: 03/12/2021
 * Time: 08:54
 * Description:
 */
public class MaterialListActivity extends AppCompatActivity implements PopupDialog.PopupDialogListener {

    private static final String TAG = "MaterialListActivity";
    @BindView(R.id.btn_add_clothes)
    Button btn_add_clothes;

    private RecyclerView rv_materialList;
    private MaterialListAdapter materialListAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private PopupDialog popupDialog;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    MyApplication app;

    int prj_id;
    int p_id;
//    int clt_id;
    int id_clicked;
    ArrayList<Clothes> materialList = new ArrayList<>();
    ArrayList<Property> propertyList = new ArrayList<>();
    ArrayList<Exploration> explorationList = new ArrayList<>();

    private MaterialListAdapter.OnItemListener onItemListener = new MaterialListAdapter.OnItemListener() {
        @Override
        public void OnItemClickListener(View view, int position) {

            int childAdapterPosition = rv_materialList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemClickListener: " +position+"child position:"+childAdapterPosition);
            Intent intent = new Intent(MaterialListActivity.this, MaterialProfileActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            id_clicked = materialList.get(childAdapterPosition).getId();
            intent.putExtra("clt_id", id_clicked);
            intent.putExtra("position", childAdapterPosition);
            startActivity(intent);
        }

        @Override
        public void OnItemLongClickListener(View view, int position) {
            int childAdapterPosition = rv_materialList.getChildAdapterPosition(view);
            Log.i(TAG, "OnItemLongClickListener: "+childAdapterPosition+"property_size:"+materialList.size());

            id_clicked = materialList.get(childAdapterPosition).getId();
            Log.i(TAG, "OnItemLongClickListener: " +"pid: "+ id_clicked);
            FragmentManager fm = getSupportFragmentManager();
            popupDialog = new PopupDialog();
            popupDialog.show(fm,"fragment_edit_delete_material");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list);
        ButterKnife.bind(this);
        setTitle("Material List");

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        updatePreference();
        initData();
        initView();

        if(prj_id==-1){
            btn_add_clothes.setEnabled(false);
        }
        propertyList = Property.getPropertyList(db, prj_id);

    }

    private void updatePreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        p_id = preferences.getInt("p_id", -1);
        prj_id = preferences.getInt("prj_id", -1);
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        materialListAdapter = new MaterialListAdapter(getData());
        materialList = getData();
        materialListAdapter = new MaterialListAdapter(materialList);
        materialListAdapter.setOnItemListener(onItemListener);
    }

    private void initView() {
        rv_materialList = (RecyclerView) findViewById(R.id.rv_material_list);
        // 设置布局管理器
        rv_materialList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_materialList.setAdapter(materialListAdapter);
    }

    @NotNull
    private ArrayList<Clothes> getData() {
        ArrayList<Clothes> data = new ArrayList<>();
        data = Clothes.getMaterialList(db,prj_id,p_id);
        Log.i(TAG, "getData: " + data.toString());
        return data;
    }


    @OnClick(R.id.btn_add_clothes)
    public void onAddClothesClick() {
        Clothes clothes = new Clothes(prj_id, p_id);
        int clt_id = clothes.insertClothes(db);
        Exploration.createExplorationList(db, p_id, prj_id, clt_id, propertyList);
        materialList = getData();
        materialListAdapter.updateData(materialList);
    }

    Intent intent;
    @OnClick(R.id.btn_gather_relax)
    public void onGatherRelaxClick() {
        intent = new Intent(MaterialListActivity.this, InteractionActivity.class);
        intent.putExtra("clt_id",-1);
        intent.putExtra("ppt_name","Relax");
        intent.putExtra("ppt_id",-2);
        startActivity(intent);
    }

    @OnClick(R.id.btn_gather_fist)
    public void onGatherFistClick() {
        intent = new Intent(MaterialListActivity.this, InteractionActivity.class);
        intent.putExtra("clt_id",-1);
        intent.putExtra("ppt_name","Fist");
        intent.putExtra("ppt_id",-3);
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



    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        FragmentManager fm = getSupportFragmentManager();
        //reuse add dialog to update
        Log.i(TAG, "onDialogEditClick: "+prj_id+"-"+ id_clicked);
        Intent intent = new Intent(MaterialListActivity.this, MaterialProfileActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("clt_id", id_clicked);
        startActivity(intent);

    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        new AlertDialog.Builder(this).setTitle("Delete Material " + id_clicked +"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override


                    public void onClick(DialogInterface dialog, int which) {
                        Clothes.deleteClothes(db,id_clicked);
                        Log.i(TAG, "delete material: "+ id_clicked);
                        materialList = getData();
                        materialListAdapter.updateData(materialList);
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
