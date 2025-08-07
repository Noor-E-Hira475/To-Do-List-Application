package com.hkproduction.todolistapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.hkproduction.todolistapp.databinding.DialogDeleteTaskBinding

class DeleteTaskDialog(
    context: Context,
    private val task: Task,
    private val onConfirmDelete: () -> Unit
) {
    private val dialog: Dialog = Dialog(context)
    private val binding: DialogDeleteTaskBinding = DialogDeleteTaskBinding.inflate(LayoutInflater.from(context))

    fun show() {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        // Set dynamic message if needed
        binding.tvConfirm.text = "Are you sure you want to delete \"${task.title}\"?"

        binding.btnConfirmDelete.setOnClickListener {
            onConfirmDelete()
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }
}
