package com.hkproduction.todolistapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class TodoDao(context: Context) {

    companion object {
        const val TABLE_TODO = "todo"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IS_DONE = "isDone"
        const val COLUMN_CREATED_AT = "createdAt"
        const val COLUMN_DEADLINE = "deadline"

        // Schema definition for creating the tasks table
        const val CREATE_TABLE_TODO = """
            CREATE TABLE $TABLE_TODO (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_IS_DONE INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_DEADLINE TEXT
            )
        """
    }

    private val dbHelper = DBManager(context.applicationContext)

    /** Inserts a new task. Returns new row ID or -1 if failed. */
    fun insert(task: Task): Long {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
            put(COLUMN_CREATED_AT, task.createdAt)
            if (!task.deadline.isNullOrEmpty()) {
                put(COLUMN_DEADLINE, task.deadline) // Store String deadline
            } else {
                putNull(COLUMN_DEADLINE) // Explicitly store null
            }
        }
        return dbHelper.writableDatabase.insert(TABLE_TODO, null, values)
    }

    /** Updates an existing task. Returns number of rows affected. */
    fun update(task: Task): Int {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
            put(COLUMN_CREATED_AT, task.createdAt)
            if (!task.deadline.isNullOrEmpty()) {
                put(COLUMN_DEADLINE, task.deadline) // Update with String
            } else {
                putNull(COLUMN_DEADLINE)
            }
        }
        return dbHelper.writableDatabase.update(
            TABLE_TODO,
            values,
            "$COLUMN_ID=?",
            arrayOf(task.id.toString())
        )
    }

    /** Deletes task by ID. Returns number of rows deleted. */
    fun delete(id: Int): Int {
        return dbHelper.writableDatabase.delete(
            TABLE_TODO,
            "$COLUMN_ID=?",
            arrayOf(id.toString())
        )
    }

    /** Retrieves a single task by ID. Returns null if not found. */
    fun getById(id: Int): Task? {
        val cursor = dbHelper.readableDatabase.query(
            TABLE_TODO,
            null,
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        val task = cursor.use {
            if (it.moveToFirst()) it.toTask() else null
        }
        return task
    }

    /** Retrieves all tasks, ordered by newest first. */
    fun getAll(): List<Task> {
        val tasks = mutableListOf<Task>()
        val cursor = dbHelper.readableDatabase.query(
            TABLE_TODO,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                tasks.add(it.toTask())
            }
        }
        return tasks
    }

    /** Converts a Cursor row into a Task object. */
    private fun Cursor.toTask(): Task {
        return Task(
            id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
            title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
            description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
            isDone = getInt(getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1,
            createdAt = getLong(getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            deadline = getStringOrNull(COLUMN_DEADLINE) // Read String deadline
        )
    }

    /** Safely retrieves a nullable String column from the Cursor. */
    private fun Cursor.getStringOrNull(columnName: String): String? {
        val index = getColumnIndexOrThrow(columnName)
        return if (isNull(index)) null else getString(index)
    }
}
