package com.example.mpi.data

data class Pilar (
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val aprovado: Boolean,
    val percentual: Double,
    val idCalendario: Int,
    val idUsuario: Int
)
