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
 * Date: 03/12/2021
 * Time: 04:20
 * Description:
 */
public class AddOpenQuestionDialog extends DialogFragment {
    private static final String TAG = "AddOpenQuestionDialog";
    Activity context;

    private TextView tv_prompt;
    private EditText et_question;


    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;
    int prj_id;
    int qst_id;
    int mode; //0:add;1:


    public AddOpenQuestionDialog(int prj_id,int qst_id) {
        super();
        this.prj_id = prj_id;
        this.qst_id = qst_id;
        mode = (qst_id == -1)? AddOpenQuestionDialog.Mode.ADD : AddOpenQuestionDialog.Mode.EDIT;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AddOpenQuestionDialog.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddParticipantDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddOpenQuestionDialog.NoticeDialogListener) context;
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
        View rootView = inflater.inflate(R.layout.dialog_add_open_question, null);

        et_question = (EditText)rootView.findViewById(R.id.et_question);
        tv_prompt = (TextView) rootView.findViewById(R.id.tv_prompt);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout\
        if (mode == AddOpenQuestionDialog.Mode.EDIT) {
            Question question = Question.getQuestion(db, qst_id);
            fillEditText(question);
        }

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton((mode == AddOpenQuestionDialog.Mode.ADD)? "add":"save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String question_con = et_question.getText().toString();
                        if (question_con.isEmpty()) {
                            Toast.makeText(context, "Fields can't be empty", Toast.LENGTH_LONG).show();
                        }else{
                            Question question = new Question(prj_id,question_con,0);

                            if(mode == AddOpenQuestionDialog.Mode.ADD){
                                int new_qst_id = question.insertQuestion(db);
                                Log.i(TAG, "onClick: "+ new_qst_id);
                            }else{
                                question.updateQuestion(db,qst_id);
                            }
                            listener.onDialogPositiveClick(AddOpenQuestionDialog.this);
                            AddOpenQuestionDialog.this.getDialog().dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddOpenQuestionDialog.this);
                    }
                });

        return builder.create();
    }

    public void fillEditText(Question question) {
        int id = question.getId();
        int prjId = question.getPrj_id();
        String content = question.getContent();
        int isMandatory = question.getIsMandatory();
        et_question.setText(content);
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
    }

    public class Mode {
        public static final int ADD = 0;
        public static final int EDIT = 1;
    }
}
