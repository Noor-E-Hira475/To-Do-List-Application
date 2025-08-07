package com.hkproduction.todolistapp

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hkproduction.todolistapp.databinding.DialogAddTaskBinding

class AddTaskDialog(
    private val context: Context,
    private val onTaskCreated: (Task) -> Unit
) {
    fun show() {
        val binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnAdd.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Title is required"
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                description = description,
                isDone = false
            )
            onTaskCreated(task)
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }
}
