package com.hkproduction.todolistapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hkproduction.todolistapp.databinding.ItemTaskBinding

class TaskAdapter(
    private var taskList: MutableList<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskDeleted: (Task, Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()
    private var filteredList: MutableList<Task> = taskList.toMutableList()

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredList[position]
        bindTaskData(holder, task, position)
    }

    override fun getItemCount(): Int = filteredList.size

    // --- Public Methods ---
    fun updateTasks(newTasks: List<Task>) {
        taskList.clear()
        taskList.addAll(newTasks)
        filter("")
    }

    fun filter(query: String) {
        filteredList = if (query.isBlank()) {
            taskList.toMutableList()
        } else {
            taskList.filter { it.matchesQuery(query) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        selectedPositions.toggle(position)
        notifyItemChanged(position)
    }

    fun clearSelection() {
        selectedPositions.clear()
        notifyDataSetChanged()
    }

    fun getSelectedTasks(): List<Task> =
        selectedPositions.map { filteredList[it] }

    // --- Private Binding Methods ---
    private fun bindTaskData(holder: TaskViewHolder, task: Task, position: Int) {
        holder.binding.apply {
            tvTitle.text = task.title

            // Hide description if empty
            if (task.description.isEmpty()) {
                tvDescription.hide()
            } else {
                tvDescription.show()
                tvDescription.text = task.description
            }
            tvDeadline.text = task.deadline // <-- Bind deadline to the UI
            cbDone.isChecked = task.isDone
            setBackgroundForSelection(this, position)

            setupCheckBoxListener(task)
            setupDeleteButton(task, position)
            setupLongClick(position)
        }
    }


    private fun setBackgroundForSelection(binding: ItemTaskBinding, position: Int) {
        binding.root.setBackgroundResource(
            if (selectedPositions.contains(position))
                R.drawable.item_task_selected_bg
            else
                R.drawable.item_task_bg
        )
    }

    private fun ItemTaskBinding.setupCheckBoxListener(task: Task) {
        cbDone.setCheckedChangeListenerSafe { isChecked ->
            task.isDone = isChecked
            onTaskChecked(task, isChecked)
        }
    }

    private fun ItemTaskBinding.setupDeleteButton(task: Task, position: Int) {
        btnDelete.setOnClickListener {
            onTaskDeleted(task, position)
        }
    }

    private fun ItemTaskBinding.setupLongClick(position: Int) {
        root.setOnLongClickListener {
            onItemLongClick(position)
            true
        }
    }


    private fun Task.matchesQuery(query: String): Boolean {
        return title.contains(query, ignoreCase = true) ||
                description.contains(query, ignoreCase = true)
    }
}
