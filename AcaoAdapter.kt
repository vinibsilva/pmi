package com.example.mpi.ui.acao

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.AcaoItemFragmentBinding
import com.example.mpi.data.Acao

class AcaoAdapter(
    private val listaAcoes: List<Acao>,
    private val onEditarClicked: (Acao) -> Unit,
    private val onExcluirClicked: (Acao) -> Unit
) : RecyclerView.Adapter<AcaoAdapter.AcaoViewHolder>() {

    inner class AcaoViewHolder(binding: AcaoItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomeAcaoItem
        val tvDescricao = binding.tvDescricaoAcaoItem
        val tvDataInicio = binding.tvDataInicioAcaoItem
        val tvDataTermino = binding.tvDataTerminoAcaoItem
        val tvResponsavel = binding.tvResponsavelAcaoItem
        val tvAprovado = binding.tvAprovadoAcaoItem
        val tvFinalizada = binding.tvFinalizadaAcaoItem
        val btnEditar = binding.btnEditarAcao
        val btnExcluir = binding.btnExcluirAcao
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoViewHolder {
        val binding = AcaoItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AcaoViewHolder, position: Int) {
        val acao = listaAcoes[position]
        holder.tvNome.text = acao.nome
        holder.tvDescricao.text = acao.descricao
        holder.tvDataInicio.text = "Início: ${acao.dataInicio}"
        holder.tvDataTermino.text = "Término: ${acao.dataTermino}"
        holder.tvResponsavel.text = "Responsável: ${acao.responsavel}"
        holder.tvAprovado.text = if (acao.aprovado) "Aprovada" else "Não Aprovada"
        holder.tvFinalizada.text = if (acao.finalizado) "Finalizada" else "Não Finalizada"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(acao)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(acao)
        }
    }

    override fun getItemCount(): Int = listaAcoes.size
}

