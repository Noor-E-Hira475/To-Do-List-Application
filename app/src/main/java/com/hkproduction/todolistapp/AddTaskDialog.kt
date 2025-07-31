package com.hkproduction.todolistapp

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hkproduction.todolistapp.databinding.DialogAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskDialog(
    private val context: Context,
    private val onTaskCreated: (Task) -> Unit
) {
    private lateinit var binding: DialogAddTaskBinding

    fun show() {
        binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        setupDeadlinePicker()
        setupButtons(dialog)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun setupDeadlinePicker() {
        binding.etDeadline.setOnClickListener {
            DateHelper.showDatePicker(context) { selectedDate ->
                binding.etDeadline.setText(selectedDate) // Always store formatted date
            }
        }
    }

    private fun setupButtons(dialog: AlertDialog) {
        binding.btnAdd.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val deadline = binding.etDeadline.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Title is required"
                return@setOnClickListener
            }

            onTaskCreated(
                Task(
                    title = title,
                    description = description,
                    isDone = false,
                    deadline = deadline // Always in "dd MMM yyyy" format
                )
            )
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}
