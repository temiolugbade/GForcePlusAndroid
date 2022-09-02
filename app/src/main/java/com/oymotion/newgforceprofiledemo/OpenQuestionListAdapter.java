package com.oymotion.newgforceprofiledemo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 02/12/2021
 * Time: 22:12
 * Description:
 */
public class OpenQuestionListAdapter extends RecyclerView.Adapter<OpenQuestionListAdapter.ViewHolder> {
    private static final String TAG = "OpenQuestionListAdapter";

    private List<Question> questionList;
    private OpenQuestionListAdapter.OnItemListener onItemListener;

    public OpenQuestionListAdapter(ArrayList<Question> data) {
        this.questionList = data;
    }

    public void updateData(ArrayList<Question> data) {
        this.questionList.clear();
        this.questionList = data;
        notifyDataSetChanged();
    }

    @Override
    public OpenQuestionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_open_question_list_item, parent, false);
        // 实例化viewholder
        OpenQuestionListAdapter.ViewHolder viewHolder = new OpenQuestionListAdapter.ViewHolder(v);

        v.setOnClickListener(new OpenQuestionListAdapter.OnItemTouchListener());
        v.setOnLongClickListener(new OpenQuestionListAdapter.OnItemTouchListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OpenQuestionListAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: "+OpenQuestionListAdapter.this.questionList.get(position).getContent());
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
        String question_con = String.valueOf(OpenQuestionListAdapter.this.questionList.get(position).getContent());
        holder.tv_question.setText(question_con);
    }


    @Override
    public int getItemCount() {
        return OpenQuestionListAdapter.this.questionList == null ? 0 : OpenQuestionListAdapter.this.questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_question;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_question = (TextView) itemView.findViewById(R.id.tv_item_open_question);
        }
    }

    public void setOnItemListener(OpenQuestionListAdapter.OnItemListener listener){
        this.onItemListener = listener;
    }


    class OnItemTouchListener implements View.OnClickListener,View.OnLongClickListener{

        @Override
        public void onClick(View v) {
            if (onItemListener != null){
                onItemListener.OnItemClickListener(v, v.getId());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemListener != null){
                onItemListener.OnItemLongClickListener(v,v.getId());
            }
            return true;
        }
    }

    public interface OnItemListener{
        void OnItemClickListener(View view, int position);
        void OnItemLongClickListener(View view, int position);
    }

}
