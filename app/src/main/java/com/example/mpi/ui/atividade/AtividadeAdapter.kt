package com.example.mpi.ui.atividade

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.data.Atividade
import com.example.mpi.databinding.FragmentAtividadeItemBinding
import com.example.mpi.ui.atividade.AtividadeAdapter.AtividadeViewHolder

class AtividadeAdapter(
    private val listaAtividades: List<Atividade>,
    private val onEditarClicked: (Atividade) -> Unit,
    private val onExcluirClicked: (Atividade) -> Unit
) : RecyclerView.Adapter<AtividadeAdapter.AtividadeViewHolder>() {

    inner class AtividadeViewHolder(binding: FragmentAtividadeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomeAtividadeItem
        val tvDescricao = binding.tvDescricaoAtividadeItem
        val tvDataInicio = binding.tvDataInicioAtividadeItem
        val tvDataTermino = binding.tvDataTerminoAtividadeItem
        val tvResponsavel = binding.tvResponsavelAtividadeItem
        val tvAprovado = binding.tvAprovadoAtividadeItem
        val tvOrcamento = binding.tvOrcamentoAtividadeItem
        val tvFinalizada = binding.tvFinalizadaAtividadeItem
        val btnEditar = binding.btnEditarAtividade
        val btnExcluir = binding.btnExcluirAtividade
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtividadeViewHolder {
        val binding = FragmentAtividadeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)  
        return AtividadeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AtividadeViewHolder, position: Int) {
        val atividade = listaAtividades[position]
        holder.tvNome.text = atividade.nome
        holder.tvDescricao.text = atividade.descricao
        holder.tvDataInicio.text = "Início: ${atividade.dataInicio}"
        holder.tvDataTermino.text = "Término: ${atividade.dataTermino}"
        holder.tvResponsavel.text = "Responsável: ${atividade.responsavel}"
        holder.tvOrcamento.text = "Orçamento: ${atividade.orcamento}"
        holder.tvOrcamento.text = "Responsável: ${atividade.responsavel}"
        holder.tvAprovado.text = if (atividade.aprovado) "Aprovada" else "Não finalizada"
        holder.tvFinalizada.text = if (atividade.finalizado) "Finalizada" else "Não finalizada"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(atividade)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(atividade)
        }
    }

    override fun getItemCount(): Int = listaAtividades.size

}
