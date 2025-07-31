package com.hkproduction.todolistapp

import android.content.Context
import android.location.GnssAntennaInfo.Listener
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.transition.Visibility
import java.text.SimpleDateFormat
import android.widget.CheckBox
import android.widget.SearchView
import androidx.appcompat.view.ActionMode
import android.content.Intent
import java.util.*

// Toast
fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

// View
fun View.setBackgroundDrawable(drawableRes: Int){
    background = ContextCompat.getDrawable(context, drawableRes)
}

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }

// checkbox
fun CheckBox.setCheckedChangeListenerSafe(listener: (Boolean) -> Unit) {
    setOnCheckedChangeListener(null) // Forget old instructions
    setOnCheckedChangeListener { _, isChecked -> listener(isChecked) } // Follow new instructions
}

// MutableSet toggle
fun <T> MutableSet<T>.toggle(item: T) {
    if (contains(item)) remove(item) else add(item)
}

// SearchView
fun SearchView.onTextChanged(listener: (String) -> Unit){
    setOnQueryTextListener(object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false
        override fun onQueryTextChange(newText: String): Boolean {
           listener(newText)
            return true
        }
    })
}

// ActionMode
fun ActionMode.updateSelectionCount(count: Int){
    title = "$count selected"
}

// Implicit Intent(Sender)
fun Context.shareText(text: String, title: String){
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT,text)
    }
    startActivity(Intent.createChooser(intent, title))
}

// update tasks
fun TaskAdapter.refreshFrom(dao: TodoDao) {
    updateTasks(dao.getAll())
}
