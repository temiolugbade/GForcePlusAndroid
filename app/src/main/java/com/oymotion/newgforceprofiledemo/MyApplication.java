package com.oymotion.newgforceprofiledemo;

import android.app.Application;
import android.util.Log;

import com.oymotion.gforceprofile.GForceProfile;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 14/07/2021
 * Time: 14:31
 * Description: make shared values accessible to many values
 */
public class MyApplication extends Application{
    private GForceProfile gForceProfile_l;
    private GForceProfile gForceProfile_r;
    private static final String TAG = "MyApplication";

    private int e_id;
    private int e_state;
    private int prj_id;
    private int itr_id;
    private int itr_type;
    private int itr_state;
    private int clt_id;
    private int clt_state;
    private int clt_count;

    private static final int ini = -1;


    @Override
    public void onCreate() {
        super.onCreate();
        e_id = ini;
        e_state = ini;
        itr_id = ini;
        itr_type = ini;
        itr_state = ini;
        clt_id = ini;
        clt_state = ini;
        clt_count = 1;

        try{
            final GForceProfile gForceProfile_int_l = new GForceProfile(new GForceProfile.GForceErrorCallback() {
                @Override
                public void onGForceErrorCallback(String errorMsg) {
                    Log.e(TAG, "error in creating instance(left): " + errorMsg);
                }
            });
            final GForceProfile gForceProfile_int_r = new GForceProfile(new GForceProfile.GForceErrorCallback() {
                @Override
                public void onGForceErrorCallback(String errorMsg) {
                    Log.e(TAG, "error in creating instance(left): " + errorMsg);
                }
            });
            setProfileLeft(gForceProfile_int_l); //初始化全局变量
            setProfileRight(gForceProfile_int_r); //初始化全局变量
        }catch (Exception e){
            Log.e("Tag", e.getMessage());
        }


    }

    public GForceProfile getProfileLeft() {

        Log.i(TAG, "getProfileLeft");
        return gForceProfile_l;
    }

    public void setProfileLeft(GForceProfile gForceProfile) {
        this.gForceProfile_l = gForceProfile;
        Log.i(TAG, "setProfileLeft");
    }

    public GForceProfile getProfileRight() {
        Log.i(TAG, "getProfileRight");
        return gForceProfile_r;
    }

    public void setProfileRight(GForceProfile gForceProfile) {

        Log.i(TAG, "setProfileRight");
        this.gForceProfile_r = gForceProfile;
    }

    //method for experiment
    public int getExperimentID() {
        return e_id;
    }

    public void setExperimentID(int e_id) {
        this.e_id = e_id;
    }

    public int getExperimentState() {
        return e_state;
    }

    public void setExperimentState(int e_state) {
        this.e_state = e_state;
    }

    //method for clothes
    public int getClothesID() {
        return clt_id;
    }

    public void setClothesID(int clt_id) {
        this.clt_id = clt_id;
    }

    public int getClothesState() {
        return clt_state;
    }

    public void setClothesState(int clt_state) {
        this.clt_state = clt_state;
    }
    public int getClothesCount() {
        return clt_count;
    }

    public void updateClothesCount(int clt_count) {
        if(clt_count == 6){
            this.clt_count = 1;
        }else{
            this.clt_count = clt_count + 1;
            Log.i(TAG, "clt_count:" + this.clt_count);
        }
    }

    //method for Interaction
    public int getInteractionID() {
        return itr_id;
    }

    public void setInteractionID(int itr_id) {
        this.itr_id = itr_id;
    }

     public int getInteractionType() {
        return itr_type;
    }

    public void setInteractionType(int itr_type) {
        this.itr_type = itr_type;
    }


    public int getInteractionState() {
        return itr_state;
    }

    public void setInteractionState(int itr_state) {
        this.e_state = itr_state;
    }


}