package com.example.myandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;
import android.net.Uri;

import android.widget.CheckBox;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    List<Task> tasks;

    AppDatabase db;

    public TaskListAdapter(AppDatabase db){
        this.db = db;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView desc;
        ImageView image;
        CheckBox done;

        public TaskViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.taskListTitle);
            desc = view.findViewById(R.id.taskListDesc);
            image = view.findViewById(R.id.taskListImage);
            done = view.findViewById(R.id.taskListDone);
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        Task task = tasks.get(position);

        holder.title.setText(task.title);
        holder.desc.setText(task.description);

        holder.done.setChecked(task.done);

        holder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {

            task.done = isChecked;

            db.taskDao().update(task);

        });

        if(task.imageUri != null){
            Uri uri = Uri.parse(task.imageUri);
            holder.image.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {

        if(tasks == null){
            return 0;
        }

        return tasks.size();
    }

    public void deleteTask(int position){

        Task task = tasks.get(position);
        db.taskDao().delete(task);
        tasks.remove(position);
        notifyItemRemoved(position);

    }
}
