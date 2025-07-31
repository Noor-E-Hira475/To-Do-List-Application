package com.hkproduction.todolistapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.hkproduction.todolistapp.databinding.DialogDeleteTaskBinding

// Simple confirmation dialog for deleting a task
class DeleteTaskDialog(
    context: Context,
    private val task: Task,
    private val onConfirmDelete: () -> Unit
) {
    private val dialog: Dialog = Dialog(context)
    private val binding = DialogDeleteTaskBinding.inflate(LayoutInflater.from(context))

    fun show() {
        dialog.apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding.tvConfirm.text = "Are you sure you want to delete \"${task.title}\"?"

        binding.btnConfirmDelete.setOnClickListener {
            onConfirmDelete()
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}
