package com.hkproduction.todolistapp

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkproduction.todolistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var todoDao: TodoDao
    private val taskList = mutableListOf<Task>()
    private lateinit var noResultsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

      noResultsText = binding.tvNoResult

        todoDao = TodoDao(this)

        setupRecyclerView()
        adapter.updateTasks(todoDao.getAll())

        setupFab()
        setUpSearchView()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            taskList,
            onTaskChecked = { task, isChecked ->
                todoDao.update(task)
                adapter.notifyItemChanged(taskList.indexOf(task))
            },
            onTaskDeleted = { task, position ->
                showDeleteConfirmationDialog(task, position)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setUpSearchView(){
      binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
          override fun onQueryTextSubmit(p0: String?): Boolean = false

          override fun onQueryTextChange(newText: String): Boolean {
              adapter.filter(newText)

              noResultsText.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
              return true
          }
      })
    }

    private fun showAddTaskDialog() {
        AddTaskDialog(this) { newTask ->
            val rowId = todoDao.insert(newTask)
            if (rowId > 0) {
                val insertedTask = todoDao.getById(rowId.toInt())
                if (insertedTask != null) {
                    val updatedTasks = todoDao.getAll()
                    adapter.updateTasks(updatedTasks)
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Task not found after insert", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to insert task", Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDeleteConfirmationDialog(task: Task, position: Int) {
        DeleteTaskDialog(this, task) {
            val deleted = todoDao.delete(task.id)
            if (deleted > 0) {
                val updatedTasks = todoDao.getAll()
                adapter.updateTasks(updatedTasks)

                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
        }.show()
    }
}
