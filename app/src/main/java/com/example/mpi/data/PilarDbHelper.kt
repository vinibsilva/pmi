package com.example.mpi.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val DB_NAME = "pmi.db"
private const val DB_VERSION = 1

class PilarDbHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sqlCreate = """
            CREATE TABLE ${PilarContract.UserEntry.TABLE_NAME} (
              ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
              ${PilarContract.UserEntry.COL_NOME} TEXT NOT NULL UNIQUE,
              ${PilarContract.UserEntry.COL_DESCRICAO} TEXT NOT NULL,
              ${PilarContract.UserEntry.COL_DATAI} TEXT NOT NULL,
              ${PilarContract.UserEntry.COL_DATAT} TEXT NOT NULL,
              ${PilarContract.UserEntry.COL_APROVADO} INTEGER DEFAULT 0,
              ${PilarContract.UserEntry.COL_PERCENTUAL} REAL DEFAULT 0.0
              
            )
        """.trimIndent()
        db.execSQL(sqlCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Se for necessário alterar esquema no futuro:
        db.execSQL("DROP TABLE IF EXISTS ${PilarContract.UserEntry.TABLE_NAME}")
        onCreate(db)
    }
}


