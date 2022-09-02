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
 * Date: 07/12/2021
 * Time: 13:27
 * Description:
 */
public class ExplorationListAdapter extends RecyclerView.Adapter<ExplorationListAdapter.ViewHolder> {

    private static final String TAG = "ExplorationListAdapter";
    private List<Exploration> explorationList;
    private ExplorationListAdapter.OnItemListener onItemListener;


    public ExplorationListAdapter(ArrayList<Exploration> data) {
        this.explorationList = data;

    }

    public void updateData(ArrayList<Exploration> data) {
        this.explorationList.clear();
        this.explorationList = data;
        notifyDataSetChanged();
    }

    @Override
    public ExplorationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_exploration_list_item, parent, false);
        // 实例化viewholder
        ExplorationListAdapter.ViewHolder viewHolder = new ExplorationListAdapter.ViewHolder(v);

        v.setOnClickListener(new ExplorationListAdapter.OnItemTouchListener());
        v.setOnLongClickListener(new ExplorationListAdapter.OnItemTouchListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExplorationListAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: "+explorationList.get(position));
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
//        String exploration_id = String.valueOf(explorationList.get(position).getId());
        String exploration_type = String.valueOf(explorationList.get(position).getPpt_name());
        holder.tv_item_exploration_type.setText(exploration_type);
//        holder.tv_item_exploration_type.setText("Exploration "+exploration_id);
        String state = String.valueOf(explorationList.get(position).getState());
        boolean b_state = (Integer.valueOf(state).equals(Exploration.State.FINISHED));
        holder.cb_state.setChecked(b_state);
    }


    @Override
    public int getItemCount() {
        return explorationList == null ? 0 : explorationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_exploration_type;
        CheckBox cb_state;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_exploration_type = (TextView) itemView.findViewById(R.id.tv_item_exploration_type);
            cb_state = (CheckBox) itemView.findViewById(R.id.cb_state);
        }
    }

    public void setOnItemListener(ExplorationListAdapter.OnItemListener listener){
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
