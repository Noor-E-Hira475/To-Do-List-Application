package com.hkproduction.todolistapp

// Represents a to-do task
data class Task(
    val id: Int = 0,
    val title: String,
    val description: String,
    var isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val deadline: String? = null
)
