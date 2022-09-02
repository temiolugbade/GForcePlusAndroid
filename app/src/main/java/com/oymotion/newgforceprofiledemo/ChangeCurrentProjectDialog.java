package com.oymotion.newgforceprofiledemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 05/12/2021
 * Time: 03:43
 * Description:
 */
public class ChangeCurrentProjectDialog extends DialogFragment {
    private static final String TAG = "ChangeCurrentProjectDialog";
    Activity context;

    private TextView tv_prompt;
    private Spinner spinner_projects;
    private List<Project> projectList;

    private ArrayAdapter<Integer> adapter;
    int selectedProject;
    int selectedPosition;



    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;

    public ChangeCurrentProjectDialog(List<Project> projectList, int current_prj) {
        super();
        this.projectList = projectList;
        this.selectedProject = current_prj;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onChangeDialogPositiveClick(DialogFragment dialog, int prj_id);
        public void onChangeDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ChangeCurrentProjectDialog.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddParticipantDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ChangeCurrentProjectDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            dbHelper = new GForceDatabaseOpenHelper(context, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_change_current_project, null);

        spinner_projects = (Spinner) rootView.findViewById(R.id.spi_projects);
        List<Integer> spi_items = createSpiList(projectList);

        adapter = new ArrayAdapter<Integer>(context,android.R.layout.simple_spinner_item,spi_items);
        Log.i(TAG, "onCreateDialog: "+projectList.isEmpty()+spi_items.toString()+adapter.toString());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_projects.setAdapter(adapter);
        selectedPosition= adapter.getPosition(selectedProject);
        spinner_projects.setSelection(selectedPosition,false);

        tv_prompt = (TextView) rootView.findViewById(R.id.tv_prompt);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout\

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedProject = Integer.valueOf(spinner_projects.getSelectedItem().toString());
                        listener.onChangeDialogPositiveClick(ChangeCurrentProjectDialog.this, selectedProject);
                        ChangeCurrentProjectDialog.this.getDialog().dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onChangeDialogNegativeClick(ChangeCurrentProjectDialog.this);
                        ChangeCurrentProjectDialog.this.getDialog().dismiss();
                    }
                });

        return builder.create();
    }

    public ArrayList<Integer> createSpiList(List<Project> projectList) {
        ArrayList<Integer> projects = new ArrayList<>();
        for(int i=0; i<projectList.size();i++){
            int id = projectList.get(i).getPrj_id();
            projects.add(id);
            Log.i(TAG, "createSpiList: "+i+"id"+id);
        }
        return projects;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
    }
}
