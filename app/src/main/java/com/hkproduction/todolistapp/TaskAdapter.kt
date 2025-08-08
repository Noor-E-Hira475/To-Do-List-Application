package com.hkproduction.todolistapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(
    private val originalList: MutableList<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskDeleted: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var filteredList: MutableList<Task> = originalList.toMutableList()


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_done)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val description: TextView = itemView.findViewById(R.id.tv_description)
        val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredList[position]
        Log.d("TaskAdapter", "Binding task: ${task.title} at position $position")

        holder.title.text = task.title
        holder.description.text = task.description

        // Prevent multiple triggers when recycling
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.isDone

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (task.isDone != isChecked) {
                task.isDone = isChecked
                onTaskChecked(task, isChecked)
            }
        }

        holder.deleteButton.setOnClickListener {
            val originalIndex = originalList.indexOf(task)
            onTaskDeleted(task, originalIndex)
        }

        val dateTextView = holder.itemView.findViewById<TextView>(R.id.textViewCreatedAt)
        val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        val formattedDate = formatter.format(Date(task.createdAt))
        dateTextView.text = formattedDate
    }

    private var currentQuery: String = ""

    // searching
    fun filter(query: String){
        currentQuery = query
        filteredList = if(query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.title.startsWith(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    //refresh
    fun updateTasks(newTasks: List<Task>){
        originalList.clear()
        originalList.addAll(newTasks)
        filter(currentQuery)
    }
    override fun getItemCount(): Int = filteredList.size
}
