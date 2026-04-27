package com.example.myandroidapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    public interface ReminderClickListener {
        void editReminder(Task task);
        void deleteReminder(Task task);
        void shareReminder(Task task);
    }

    private final Context context;
    private final ReminderClickListener listener;
    private List<Task> tasks = new ArrayList<>();

    public TaskListAdapter(Context context, ReminderClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        TextView info;
        ImageView image;
        Button editButton;
        Button shareButton;
        Button deleteButton;

        public TaskViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.taskListTitle);
            desc = view.findViewById(R.id.taskListDesc);
            info = view.findViewById(R.id.taskListDue);
            image = view.findViewById(R.id.taskListImage);
            editButton = view.findViewById(R.id.editButton);
            shareButton = view.findViewById(R.id.shareButton);
            deleteButton = view.findViewById(R.id.deleteButton);
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

        String source = task.shared ? "Shared by other user" : "Created by me";
        String due = "Due: " + emptyText(task.dueDate) + " " + emptyText(task.dueTime);
        String location = task.hasLocation
                ? "Location saved: " + task.latitude + ", " + task.longitude
                : "No location";
        holder.info.setText(source + "\n" + due + "\n" + location);

        if (task.imageUri == null || task.imageUri.isEmpty()) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageURI(Uri.parse(task.imageUri));
        }

        holder.editButton.setEnabled(!task.shared);
        holder.editButton.setOnClickListener(v -> listener.editReminder(task));
        holder.deleteButton.setOnClickListener(v -> listener.deleteReminder(task));
        holder.shareButton.setEnabled(!task.shared);
        holder.shareButton.setOnClickListener(v -> listener.shareReminder(task));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private String emptyText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text;
    }
}
