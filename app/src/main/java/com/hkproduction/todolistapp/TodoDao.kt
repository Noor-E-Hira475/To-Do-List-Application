package com.hkproduction.todolistapp

import android.content.ContentValues
import android.content.Context

class TodoDao(context: Context) {

    companion object {
        const val TABLE_TODO = "todo"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IS_DONE = "isDone"
        const val COLUMN_CREATED_AT = "createdAt"

        const val CREATE_TABLE_TODO = """
            CREATE TABLE $TABLE_TODO (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_IS_DONE INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """
    }

    private val dbHelper = DBManager(context.applicationContext)

    fun insert(task: Task): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
            put(COLUMN_CREATED_AT, task.createdAt)
        }
        return db.insert(TABLE_TODO, null, values)
    }

    fun update(task: Task): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
            put(COLUMN_CREATED_AT, task.createdAt)
        }
        return db.update(
            TABLE_TODO,
            values,
            "$COLUMN_ID=?",
            arrayOf(task.id.toString())
        )
    }

    fun delete(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            TABLE_TODO,
            "$COLUMN_ID=?",
            arrayOf(id.toString())
        )
    }

    fun getById(id: Int): Task? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_TODO,
            null,
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var task: Task? = null
        if (cursor.moveToFirst()) {
            task = Task(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1,
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            )
        }
        cursor.close()
        return task
    }

    fun getAll(): List<Task> {
        val list = mutableListOf<Task>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_TODO,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )
        while (cursor.moveToNext()) {
            val task = Task(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1,
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            )
            list.add(task)
        }
        cursor.close()
        return list
    }
}
