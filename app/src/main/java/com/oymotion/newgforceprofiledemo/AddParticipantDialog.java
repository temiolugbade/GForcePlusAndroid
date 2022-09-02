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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 01/12/2021
 * Time: 02:39
 * Description:
 */
public class AddParticipantDialog extends DialogFragment {
    private static final String TAG = "AddParticipantDialog";

    Activity context;

//    @BindView(R.id.et_participant_id)
    EditText et_p_id;
    private TextView tv_prompt;



    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;
    int mode; //0:add;1:
    int p_id;
    int prj_id;

    public AddParticipantDialog( int prj_id, int p_id ) {
        super();
        this.p_id = p_id;
        this.prj_id = prj_id;
        mode = (p_id == -1)? AddProjectDialog.Mode.ADD : AddProjectDialog.Mode.EDIT;
        Log.i(TAG, "AddParticipantDialog: mode:"+mode);
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int p_id);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddParticipantDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
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
        View rootView = inflater.inflate(R.layout.dialog_add_participant, null);
        et_p_id = (EditText)rootView.findViewById(R.id.et_participant_id);
        tv_prompt = (TextView)rootView.findViewById(R.id.tv_prompt);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout\
        if (mode == AddParticipantDialog.Mode.EDIT) {
            Participant participant = Participant.getParticipant(db,prj_id,p_id);
            fillEditText(participant);
        }
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton((mode == AddProjectDialog.Mode.ADD)? "add":"save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String id_str = et_p_id.getText().toString();
                        if (!id_str.isEmpty()) {
                            int new_p_id = Integer.valueOf(id_str);
                            if(Participant.isIDExist(db,prj_id,new_p_id) && mode == AddProjectDialog.Mode.ADD){
                                Toast.makeText(context, "Participant ID Exist.", Toast.LENGTH_LONG).show();
                                AddParticipantDialog.this.getDialog().show();
                            }else{
                                Participant participant = new Participant(new_p_id,prj_id,0);
                                Log.i(TAG, "onClick: "+new_p_id);

                                if(mode == AddProjectDialog.Mode.ADD){
                                    participant.insertParticipant(db);
                                }else{
                                    participant.updateParticipant(db,prj_id,p_id,new_p_id);
                                }

                                listener.onDialogPositiveClick(AddParticipantDialog.this, new_p_id);
                                AddParticipantDialog.this.getDialog().dismiss();
                            }
                        } else {
                            Toast.makeText(context, "Please enter Participant ID with correct format.", Toast.LENGTH_LONG).show();
                            AddParticipantDialog.this.getDialog().show();
                        }
                        Log.i(TAG, "onClick: "+id_str);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddParticipantDialog.this);
                    }
                });

        return builder.create();
    }
    public void fillEditText(Participant participant) {
//        int id = participant.getId();
        int prj_id = participant.getPrj_id();
        int p_id = participant.getP_id();
        int state = participant.getState();

        et_p_id.setText(String.valueOf(p_id));

    }


    @Override
    public void onStart() {
        super.onStart();
//        getDialog().setCanceledOnTouchOutside(false);
    }

    public class Mode {
        public static final int ADD = 0;
        public static final int EDIT = 1;
    }
}
