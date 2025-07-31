package com.hkproduction.todolistapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkproduction.todolistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var todoDao: TodoDao
    private lateinit var noResultsText: TextView

    // Tracks multi-selection mode (e.g., when selecting tasks to share)
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeToolbar.customToolbar)

        todoDao = TodoDao(this)
        noResultsText = binding.tvNoResult

        setupRecyclerView()
        adapter.refreshFrom(todoDao) // Load saved tasks initially
        setupFab()
        setupSearch()
    }

    // Sets up the RecyclerView with the adapter and defines item actions
    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            mutableListOf(),
            onTaskChecked = { task, _ -> todoDao.update(task) },
            onTaskDeleted = { task, _ -> showDeleteConfirmationDialog(task) },
            onItemLongClick = { position -> handleItemLongClick(position) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Handles long press on an item to enable selection mode
    private fun handleItemLongClick(position: Int) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback)
        }
        adapter.toggleSelection(position)
        actionMode?.updateSelectionCount(adapter.getSelectedTasks().size)
    }

    // Defines what happens during multi-selection mode (action bar actions)
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            menuInflater.inflate(R.menu.main_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_share -> {
                    shareSelectedTasks()
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter.clearSelection()
            actionMode = null
        }
    }

    // Shares the selected tasks through system share options
    private fun shareSelectedTasks() {
        val selectedTasks = adapter.getSelectedTasks()
        if (selectedTasks.isEmpty()) {
            showToast("No tasks selected")
            return
        }
        val textToShare = selectedTasks.joinToString("\n") { task ->
            val deadlineText = task.deadline?: "No deadline"
            "${task.title} - ${task.description} - Deadline: $deadlineText"
        }
        shareText(textToShare, "Share Via")
    }

    // Sets up Floating Action Button to add a new task
    private fun setupFab() {
        binding.fabAdd.setOnClickListener { showAddTaskDialog() }
    }

    // Handles live search in the task list
    private fun setupSearch() {
        binding.searchBar.onTextChanged { query ->
            adapter.filter(query)
            if (adapter.itemCount == 0) noResultsText.show() else noResultsText.hide()
        }
    }

    // Shows a dialog for adding a task and inserts it into the database
    private fun showAddTaskDialog() {
        AddTaskDialog(this) { newTask ->
            // Directly insert task, deadline can be null/empty
            if (todoDao.insert(newTask) > 0) {
                adapter.refreshFrom(todoDao)
                showToast("Task added")
            } else {
                showToast("Failed to insert task")
            }
        }.show()
    }


    // Shows a dialog asking the user to confirm deleting a task
    private fun showDeleteConfirmationDialog(task: Task) {
        DeleteTaskDialog(this, task) {
            if (todoDao.delete(task.id) > 0) {
                adapter.refreshFrom(todoDao)
                showToast("Task deleted")
            } else {
                showToast("Failed to delete task")
            }
        }.show()
    }
}
