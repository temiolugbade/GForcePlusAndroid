package com.oymotion.newgforceprofiledemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 06/12/2021
 * Time: 00:02
 * Description:
 */
public class PopupDialog extends DialogFragment {

    private static final String TAG = "PopupDialog";

    public PopupDialog() {
        super();
    }
    public interface PopupDialogListener {
        public void onDialogEditClick(androidx.fragment.app.DialogFragment dialog);
        public void onDialogDeleteClick(androidx.fragment.app.DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PopupDialog.PopupDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddProjectDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (PopupDialog.PopupDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i(TAG, "onStart: ");

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM; // 显示在底部
        params.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度填充满屏
        window.setAttributes(params);

        int color = getResources().getColor(android.R.color.transparent);
        window.setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView: ");

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); // 不显示标题栏

        final View dialogView = inflater.inflate(R.layout.dialog_popup, container, false);

        Button btn_edit = dialogView.findViewById(R.id.btn_edit);
        Button btn_delete = dialogView.findViewById(R.id.btn_delete);
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "edit clicked", Toast.LENGTH_SHORT).show();
                startDownAnimation(dialogView);
                listener.onDialogEditClick(PopupDialog.this);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "delete clicked", Toast.LENGTH_SHORT).show();
                startDownAnimation(dialogView);
                listener.onDialogDeleteClick(PopupDialog.this);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                startDownAnimation(dialogView);
            }
        });

        startUpAnimation(dialogView);

        return dialogView;
    }

    private void startUpAnimation(View view) {
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
    }

    private void startDownAnimation(View view) {
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(slide);
    }

}
