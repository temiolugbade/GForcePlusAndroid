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
 * Time: 09:13
 * Description:
 */
public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.ViewHolder> {
    private static final String TAG = "MaterialListAdapter";
    private List<Clothes> materialList;
    private MaterialListAdapter.OnItemListener onItemListener;


    public MaterialListAdapter(ArrayList<Clothes> data) {
        this.materialList = data;

    }

    public void updateData(ArrayList<Clothes> data) {
        this.materialList.clear();
        this.materialList = data;
        notifyDataSetChanged();
    }

    @Override
    public MaterialListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_material_list_item, parent, false);
        // 实例化viewholder
        MaterialListAdapter.ViewHolder viewHolder = new MaterialListAdapter.ViewHolder(v);

        v.setOnClickListener(new MaterialListAdapter.OnItemTouchListener());
        v.setOnLongClickListener(new MaterialListAdapter.OnItemTouchListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MaterialListAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: "+materialList.get(position));
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
        String clt_id = String.valueOf(materialList.get(position).getId());
        Log.i(TAG, "onBindViewHolder: "+"clt_id:"+clt_id);
        int no = position + 1;
        holder.tv_item_material_no.setText("Material "+no);
        String state = String.valueOf(materialList.get(position).getState());
        boolean b_state = (Integer.valueOf(state).equals(Clothes.State.FINISHED));
        holder.cb_state.setChecked(b_state);
    }


    @Override
    public int getItemCount() {
        return materialList == null ? 0 : materialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_material_no;
        CheckBox cb_state;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_material_no = (TextView) itemView.findViewById(R.id.tv_item_material_no);
            cb_state = (CheckBox) itemView.findViewById(R.id.cb_state);
        }
    }

    public void setOnItemListener(MaterialListAdapter.OnItemListener listener){
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
