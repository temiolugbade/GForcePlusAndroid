package com.oymotion.newgforceprofiledemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;


public class MidInteractionDialogFragment extends DialogFragment {


    @BindView(R.id.dialog_response)
    EditText txt_dialog_response;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(
                R.layout.dialog_midinteraction, null);

        txt_dialog_response = view.findViewById(R.id.dialog_response);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setTitle(R.string.mid_interaction_query)
                .setPositiveButton(R.string.mid_interaction_complete_button,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.getResponse(txt_dialog_response.getText().toString());
                        //listener.onDialogPositiveClick(MidInteractionDialogFragment.this);

                    }
                });




        return builder.create();
    }




    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface MidInteractionDialogListener {
        //public void onDialogPositiveClick(DialogFragment dialog);

        public void getResponse(String response);
    }

    // Use this instance of the interface to deliver action events
    MidInteractionDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the MidInteractionDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MidInteractionDialogListener so we can send events to the host
            listener = (MidInteractionDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement MidInteractionDialogListener");
        }
    }

}

