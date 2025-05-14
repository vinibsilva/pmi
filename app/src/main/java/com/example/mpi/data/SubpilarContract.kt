package com.example.mpi.data
import android.provider.BaseColumns

object SubpilarContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "subpilar"
        const val COL_NOME   = "nome"
        const val COL_DESCRICAO  = "descricao"
        const val COL_DATAI   = "dataInicio"
        const val COL_DATAT   = "dataTermino"
        const val COL_APROVADO   = "isAprovado"
    }
}