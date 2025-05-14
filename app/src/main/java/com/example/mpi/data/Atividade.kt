package com.example.mpi.data

data class Atividade(
    val id: Long,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val responsavel: Int,
    val aprovado: Int,
    val finalizado: Int,
    val orcamento: Double
)