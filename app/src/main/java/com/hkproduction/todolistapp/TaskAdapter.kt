package com.hkproduction.todolistapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView


class TaskAdapter(
    private var taskList: MutableList<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskDeleted: (Task, Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()
    private var filteredList: MutableList<Task> = taskList.toMutableList()

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cb_done)
        val title: TextView = view.findViewById(R.id.tv_title)
        val description: TextView = view.findViewById(R.id.tv_description)
        val deleteBtn: ImageView = view.findViewById(R.id.btn_delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredList[position]

        holder.title.text = task.title
        holder.description.text = task.description
        holder.checkBox.isChecked = task.isDone

        // Preserve rounded corners + show selection background
        holder.itemView.background = ContextCompat.getDrawable(
            holder.itemView.context,
            if (selectedPositions.contains(position))
                R.drawable.item_task_selected_bg
            else
                R.drawable.item_task_bg
        )

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isDone = isChecked
            onTaskChecked(task, isChecked)
        }

        holder.deleteBtn.setOnClickListener {
            onTaskDeleted(task, position)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateTasks(newTasks: List<Task>) {
        taskList.clear()
        taskList.addAll(newTasks)
        filter("") // reset filter
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            taskList.toMutableList()
        } else {
            taskList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        notifyItemChanged(position)
    }

    fun clearSelection() {
        selectedPositions.clear()
        notifyDataSetChanged()
    }

    fun getSelectedTasks(): List<Task> {
        return selectedPositions.map { filteredList[it] }
    }

}
