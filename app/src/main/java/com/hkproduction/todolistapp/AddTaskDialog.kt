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
    fun show() {
        val binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        val calendar = Calendar.getInstance()

        binding.etDeadline.setOnClickListener {
            android.app.DatePickerDialog(
                context,
                {_, year, month, day -> calendar.set(year, month, day)
                val formattedDate = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(calendar.time)
                binding.etDeadline.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.btnAdd.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val deadlineText = binding.etDeadline.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Title is required"
                return@setOnClickListener
            }

            val deadlineMillis = if (deadlineText.isNotEmpty()) {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .parse(deadlineText)?.time
            } else {
                null
            }

                val task = Task(
                title = title,
                description = description,
                isDone = false,
                deadline = deadlineMillis
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
