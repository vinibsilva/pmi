package com.example.mpi.data
import android.provider.BaseColumns

object PilarContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "pilar"
        const val COL_NOME   = "nome"
        const val COL_DESCRICAO  = "descricao"
        const val COL_DATAI   = "dataInicio"
        const val COL_DATAT   = "dataTermino"
        const val COL_APROVADO   = "isAprovado"
        const val COL_PERCENTUAL = "percentual"
    }
}

