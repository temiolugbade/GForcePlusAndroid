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
 * Date: 05/12/2021
 * Time: 02:32
 * Description:
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    private static final String TAG = "ProjectListAdapter";

    private List<Project> projectList;
    private OnItemListener onItemListener;


    public ProjectListAdapter(ArrayList<Project> data) {
        this.projectList = data;
    }

    public void updateData(ArrayList<Project> data) {
        this.projectList.clear();
        this.projectList = data;
        notifyDataSetChanged();
    }

    @Override
    public ProjectListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_project_list_item, parent, false);
        // 实例化viewholder
        ProjectListAdapter.ViewHolder viewHolder = new ProjectListAdapter.ViewHolder(v);

        v.setOnClickListener(new OnItemTouchListener());
        v.setOnLongClickListener(new OnItemTouchListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProjectListAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + ProjectListAdapter.this.projectList.get(position).getPrj_id());
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
        String project_id = String.valueOf(ProjectListAdapter.this.projectList.get(position).getPrj_id());
        String project_name = String.valueOf(ProjectListAdapter.this.projectList.get(position).getPrj_name());
        String researcher = String.valueOf(ProjectListAdapter.this.projectList.get(position).getResearcher());
        holder.tv_project_id.setText(project_id);
        holder.tv_project_name.setText(project_name);
        holder.tv_researcher.setText(researcher);
    }


    @Override
    public int getItemCount() {
        return ProjectListAdapter.this.projectList == null ? 0 : ProjectListAdapter.this.projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_project_id;
        TextView tv_project_name;
        TextView tv_researcher;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_project_id = (TextView) itemView.findViewById(R.id.tv_item_prj_id);
            tv_project_name = (TextView) itemView.findViewById(R.id.tv_item_prj_name);
            tv_researcher = (TextView) itemView.findViewById(R.id.tv_item_researcher);
        }
    }




    public void setOnItemListener(OnItemListener listener){
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

