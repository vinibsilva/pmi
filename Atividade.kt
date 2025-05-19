package com.example.mpi.data

data class Atividade(
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val responsavel: Int,
    val aprovado: Boolean,
    val finalizado: Boolean,
    val orcamento: Double,
    val idAcao: Long,
    val idUsuario: Long
)