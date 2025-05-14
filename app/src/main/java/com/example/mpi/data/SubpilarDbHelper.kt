package com.example.mpi.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val DB_NAME = "pmi.db"
private const val DB_VERSION = 1

class SubpilarDbHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sqlCreate = """
            CREATE TABLE ${SubpilarContract.UserEntry.TABLE_NAME} (
              ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
              ${SubpilarContract.UserEntry.COL_NOME} TEXT NOT NULL UNIQUE,
              ${SubpilarContract.UserEntry.COL_DESCRICAO} TEXT NOT NULL,
              ${SubpilarContract.UserEntry.COL_DATAI} TEXT NOT NULL,
              ${SubpilarContract.UserEntry.COL_DATAT} TEXT NOT NULL,
              ${SubpilarContract.UserEntry.COL_APROVADO} INTEGER DEFAULT 0
              
            )
        """.trimIndent()
        db.execSQL(sqlCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Se for necessário alterar esquema no futuro:
        db.execSQL("DROP TABLE IF EXISTS ${SubpilarContract.UserEntry.TABLE_NAME}")
        onCreate(db)
    }
}