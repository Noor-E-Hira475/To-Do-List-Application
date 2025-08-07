package com.hkproduction.todolistapp

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String,
    var isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
