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
 * Date: 03/12/2021
 * Time: 10:51
 * Description:
 */
public class InteractionListAdapter extends RecyclerView.Adapter<InteractionListAdapter.ViewHolder> {
    private static final String TAG = "InteractionListAdapter";

    private List<Interaction> interactionList;


    public InteractionListAdapter(ArrayList<Interaction> data) {
        this.interactionList = data;
    }

    public void updateData(ArrayList<Interaction> data) {
        this.interactionList.clear();
        this.interactionList = data;
        notifyDataSetChanged();
    }

    @Override
    public InteractionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_interaction_list_item, parent, false);
        // 实例化viewholder
        InteractionListAdapter.ViewHolder viewHolder = new InteractionListAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InteractionListAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + interactionList.get(position).getPid());
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
        String p_id = String.valueOf(interactionList.get(position).getPid());
        String type = String.valueOf(interactionList.get(position).getType());
        holder.tv_item_interaction.setText(type);
        String state = String.valueOf(interactionList.get(position).getState());
        boolean b_state = (Integer.valueOf(state).equals(Interaction.State.FINISHED));
        holder.cb_state.setChecked(b_state);
    }


    @Override
    public int getItemCount() {
        return interactionList == null ? 0 : interactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_interaction;
        CheckBox cb_state;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_interaction = (TextView) itemView.findViewById(R.id.tv_item_interaction_type);
            cb_state = (CheckBox) itemView.findViewById(R.id.cb_state);
        }
    }

}