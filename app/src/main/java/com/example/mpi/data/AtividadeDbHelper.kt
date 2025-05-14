package com.example.mpi.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val DB_NAME = "pmi.db"
private const val DB_VERSION = 1

class AtividadeDbHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sqlCreate = """
            CREATE TABLE ${AtividadeContract.UserEntry.TABLE_NAME} (
              ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
              ${AtividadeContract.UserEntry.COL_NOME} TEXT NOT NULL UNIQUE,
              ${AtividadeContract.UserEntry.COL_DESCRICAO} TEXT NOT NULL,
              ${AtividadeContract.UserEntry.COL_RESPONSAVEL} INTEGER NOT NULL,
              ${AtividadeContract.UserEntry.COL_DATAI} TEXT NOT NULL,
              ${AtividadeContract.UserEntry.COL_ORCAMENTO} REAL NOT NULL,
              ${AtividadeContract.UserEntry.COL_DATAT} TEXT NOT NULL,
              ${AtividadeContract.UserEntry.COL_APROVADO} INTEGER DEFAULT 0,
              ${AtividadeContract.UserEntry.COL_FINALIZADO} INTEGER DEFAULT 0
              
            )
        """.trimIndent()
        db.execSQL(sqlCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Se for necessário alterar esquema no futuro:
        db.execSQL("DROP TABLE IF EXISTS ${AtividadeContract.UserEntry.TABLE_NAME}")
        onCreate(db)
    }
}