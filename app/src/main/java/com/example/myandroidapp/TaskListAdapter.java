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
/* The above imports are used to allow for code to use classes, methods, and resources
 from other packages already available in android studio without the need to make all
  code from scratch */

/* TaskListAdapter is responsible for displaying
tasks inside the RecyclerView list in ToDoListActivity. */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    // List that stores all tasks retrieved from the database
    List<Task> tasks;

    // Reference to the Room database so tasks can be updated or deleted
    AppDatabase db;

    // Constructor receives the database instance
    public TaskListAdapter(AppDatabase db){
        this.db = db;
    }

    /* setTasks updates the adapter with the new list of tasks and refreshes
     the RecyclerView display. */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

     /* holds references to the UI elements
      for each individual task item in the list. */
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

    /* Called when RecyclerView needs to create a new item view.
    The layout file task_layout.xml defines how each task item appears on screen. */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }

    /* Binds the task data to the UI elements in the ViewHolder.
    Runs for each item in the list. */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        // Get the task at the current position
        Task task = tasks.get(position);

        // Display task title and description
        holder.title.setText(task.title);
        holder.desc.setText(task.description);

        // Set checkbox based on whether task is completed
        holder.done.setChecked(task.done);

        // When the checkbox is changed, update the 'done' status in the database.
        holder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {

            task.done = isChecked;

            db.taskDao().update(task);

        });

        // If the task has an associated image, display it in the ImageView.
        if(task.imageUri != null){
            Uri uri = Uri.parse(task.imageUri);
            holder.image.setImageURI(uri);
        }
    }

    /* Returns the number of tasks that should be displayed in the RecyclerView. */
    @Override
    public int getItemCount() {

        if(tasks == null){
            return 0;
        }

        return tasks.size();
    }

    /* Deletes a task from both the database and the RecyclerView list.
    This method is triggered when a user swipes a task left or right in the list. */
    public void deleteTask(int position){

        Task task = tasks.get(position);
        db.taskDao().delete(task);
        tasks.remove(position);
        notifyItemRemoved(position);

    }
}
