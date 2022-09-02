package com.oymotion.newgforceprofiledemo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
 * Time: 00:55
 * Description:
 */
public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ViewHolder> {
    private static final String TAG = "ParticipantListAdapter";

    private List<Participant> participantList;
    private ParticipantListAdapter.OnItemListener onItemListener;


    public ParticipantListAdapter(ArrayList<Participant> data) {
        this.participantList = data;
    }

    public void updateData(ArrayList<Participant> data) {
        this.participantList.clear();
        this.participantList = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_participant_list_item, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);

        v.setOnClickListener(new ParticipantListAdapter.OnItemTouchListener());
        v.setOnLongClickListener(new ParticipantListAdapter.OnItemTouchListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: "+participantList.get(position).getP_id());
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
        String p_id = String.valueOf(participantList.get(position).getP_id());
        holder.tv_p_id.setText(p_id);
        String state = String.valueOf(participantList.get(position).getState());
        boolean b_state = (Integer.valueOf(state).equals(Participant.State.COMPLETE));
        holder.cb_state.setChecked(b_state);
    }


    @Override
    public int getItemCount() {
        return participantList == null ? 0 : participantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_p_id;
        CheckBox cb_state;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_p_id = (TextView) itemView.findViewById(R.id.item_tv);
            cb_state = (CheckBox) itemView.findViewById(R.id.cb_state);
        }
    }



    public void setOnItemListener(ParticipantListAdapter.OnItemListener listener){
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
