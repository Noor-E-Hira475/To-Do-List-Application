package com.hkproduction.todolistapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkproduction.todolistapp.databinding.ActivityMainBinding
import androidx.appcompat.view.ActionMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var todoDao: TodoDao
    private lateinit var noResultsText: TextView

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.custom_toolbar))
        findViewById<TextView>(R.id.toolbar_title).text = getString(R.string.to_do_list)

        noResultsText = binding.tvNoResult
        todoDao = TodoDao(this)

        setupRecyclerView()
        adapter.updateTasks(todoDao.getAll())
        setupFab()
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            mutableListOf(),
            onTaskChecked = { task, _ -> todoDao.update(task) },
            onTaskDeleted = { task, _ -> showDeleteConfirmationDialog(task) },
            onItemLongClick = { position ->
                if (actionMode == null) {
                    actionMode = startSupportActionMode(actionModeCallback)
                }
                adapter.toggleSelection(position)
                actionMode?.title = "${adapter.getSelectedTasks().size} selected"
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

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

    private fun shareSelectedTasks() {
        val selectedTasks = adapter.getSelectedTasks()
        if (selectedTasks.isEmpty()) {
            Toast.makeText(this, "No tasks selected", Toast.LENGTH_SHORT).show()
            return
        }
        val textToShare = selectedTasks.joinToString("\n") { "${it.title} - ${it.description}" }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textToShare)
        }
        startActivity(Intent.createChooser(intent, "Share tasks via"))
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener { showAddTaskDialog() }
    }

    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter(newText)
                noResultsText.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                return true
            }
        })
    }

    private fun showAddTaskDialog() {
        AddTaskDialog(this) { newTask ->
            val finalTask = if (newTask.deadline == 0L) {
                newTask.copy(deadline = null)
            } else newTask

            if (todoDao.insert(finalTask) > 0) {
                adapter.updateTasks(todoDao.getAll())
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to insert task", Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        DeleteTaskDialog(this, task) {
            if (todoDao.delete(task.id) > 0) {
                adapter.updateTasks(todoDao.getAll())
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
        }.show()
    }
}
