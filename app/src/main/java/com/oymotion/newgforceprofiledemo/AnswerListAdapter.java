package com.oymotion.newgforceprofiledemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 08/12/2021
 * Time: 00:16
 * Description:
 */
public class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.ViewHolder> {

    private static final String TAG = "AnswerListAdapter";
    private List<Question> questionList;


    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    int clt_id;
    boolean isEditing;

    public AnswerListAdapter(SQLiteDatabase db,ArrayList<Question> questionList,int clt_id) {
        this.questionList = questionList;
        this.clt_id = clt_id;
        this.db = db;

    }

    public void updateData(ArrayList<Question> questionList,ArrayList<Answer> answerList) {
        this.questionList.clear();
        this.questionList = questionList;
//        this.answerList.clear();
//        this.answerList = answerList;
        notifyDataSetChanged();
    }

    @Override
    public AnswerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_answer_list_item, parent, false);
        // 实例化viewholder
        AnswerListAdapter.ViewHolder viewHolder = new AnswerListAdapter.ViewHolder(v);
        context = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AnswerListAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + questionList.get(position));
//        Log.i(TAG, "onBindViewHolder: " + answerList.get(position));
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)

        String question = String.valueOf(questionList.get(position).getContent());
        int question_id = questionList.get(position).getId();
        holder.tv_item_question.setText(question);
        String isMandatory = String.valueOf(questionList.get(position).getIsMandatory());
        boolean b_isMandatory = (Integer.valueOf(isMandatory).equals(1));


        Answer answer = Answer.getQuestionAnswer(db,question_id,clt_id);
        if (answer != null){
            String answer_content = answer.getContent();
            holder.et_item_answer.setText(answer_content);
            holder.btn_save_answer.setText("EDIT");
            holder.et_item_answer.setFocusableInTouchMode(false);
            holder.et_item_answer.setFocusable(false);
            isEditing = false;
        }else{
            holder.btn_save_answer.setText("Save");
            isEditing = true;
            holder.et_item_answer.setFocusable(true);
            holder.et_item_answer.setFocusableInTouchMode(true);
            holder.et_item_answer.requestFocus();
        }

//        String new_answer = holder.et_item_answer.getText().toString();
        int p_id = Participant.getIDFromPreference(context);
        int prj_id = Project.getIDFromPreference(context);

        holder.btn_save_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_answer = holder.et_item_answer.getText().toString();
                if(isEditing){
                    if (answer == null){
                        if (new_answer.isEmpty()){
                            Toast.makeText(context, "PLease enter content", Toast.LENGTH_LONG).show();
                        }else{
                            Answer answer = new Answer(p_id, prj_id, clt_id, question_id, new_answer);
                            answer.insertAnswer(db);
                            isEditing = false;
                            holder.btn_save_answer.setText("Edit");
                            holder.et_item_answer.setFocusableInTouchMode(false);
                            holder.et_item_answer.setFocusable(false);
                            isEditing = false;
                        }
                    }else{
                        if (new_answer.isEmpty()){
                            Toast.makeText(context, "PLease enter content", Toast.LENGTH_LONG).show();
                        }else{
//                            Answer answer = new Answer(p_id, prj_id, clt_id, question_id, new_answer);
                            Answer.updateContent(db, clt_id, question_id, new_answer);
                            isEditing = false;
                            holder.btn_save_answer.setText("Edit");
                            holder.et_item_answer.setFocusableInTouchMode(false);
                            holder.et_item_answer.setFocusable(false);
                            isEditing = false;
                        }

                    }
                }else{
                    holder.btn_save_answer.setText("SAVE");
                    holder.et_item_answer.setFocusable(true);
                    holder.et_item_answer.setFocusableInTouchMode(true);
                    holder.et_item_answer.requestFocus();
                    isEditing = true;
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_question;
        EditText et_item_answer;
        Button btn_save_answer;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_question = (TextView) itemView.findViewById(R.id.tv_item_question);
            et_item_answer = (EditText) itemView.findViewById(R.id.et_item_answer);
            btn_save_answer = (Button) itemView.findViewById(R.id.btn_save_answer);
        }
    }





}
