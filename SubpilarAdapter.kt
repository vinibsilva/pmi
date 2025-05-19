package com.example.mpi.ui.subpilar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.SubpilarItemFragmentBinding
import com.example.mpi.data.Subpilar

class SubpilarAdapter(
    private val listaSubpilares: List<Subpilar>,
    private val onEditarClicked: (Subpilar) -> Unit,
    private val onExcluirClicked: (Subpilar) -> Unit
) : RecyclerView.Adapter<SubpilarAdapter.SubpilarViewHolder>() {

    inner class SubpilarViewHolder(binding: SubpilarItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomeSubpilarItem
        val tvDescricao = binding.tvDescricaoSubpilarItem
        val tvDataInicio = binding.tvDataInicioSubpilarItem
        val tvDataTermino = binding.tvDataTerminoSubpilarItem
        val tvAprovado = binding.tvAprovadoSubpilarItem
        val btnEditar = binding.btnEditarSubpilar
        val btnExcluir = binding.btnExcluirSubpilar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubpilarViewHolder {
        val binding = SubpilarItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubpilarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubpilarViewHolder, position: Int) {
        val subpilar = listaSubpilares[position]
        holder.tvNome.text = subpilar.nome
        holder.tvDescricao.text = subpilar.descricao
        holder.tvDataInicio.text = "Início: ${subpilar.dataInicio}"
        holder.tvDataTermino.text = "Término: ${subpilar.dataTermino}"
        holder.tvAprovado.text = if (subpilar.aprovado) "Aprovado" else "Pendente"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(subpilar)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(subpilar)
        }
    }
    override fun getItemCount(): Int = listaSubpilares.size
}