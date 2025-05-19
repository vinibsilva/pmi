package com.example.mpi.data

data class Subpilar(
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val aprovado: Boolean,
    val idPilar: Long,
    val idUsuario: Long
)